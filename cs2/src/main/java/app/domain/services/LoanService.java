package app.domain.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.NotFoundException;
import app.domain.models.BankAccount;
import app.domain.models.OperationsLog;
import app.domain.models.Loan;
import app.domain.models.enums.LoanStatus;
import app.domain.models.enums.LoanType;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import app.domain.ports.LoanPort;


public class LoanService {

    private final LoanPort loanPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort bitacoraPort;
    private final ClientService clientService;

    public LoanService(LoanPort loanPort,
                       BankAccountPort bankAccountPort,
                       OperationsLogPort bitacoraPort,
                       ClientService clientService) {
        this.loanPort = loanPort;
        this.bankAccountPort = bankAccountPort;
        this.bitacoraPort = bitacoraPort;
        this.clientService = clientService;
    }

    /**
     * Crea una nueva solicitud de préstamo en estado UnderReview.
     */
    public Loan createLoanRequest(String clientRequestorId,
                                   LoanType loanType,
                                   BigDecimal requestedAmount,
                                   Integer termMonths,
                                   String destinationAccountDisbursement,
                                   Long creatorUserId,
                                   String creatorRole) {

        // 1. Validar que el cliente exista y esté activo
        clientService.validateClientIsActive(clientRequestorId);

        // 2. Validar monto y plazo
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Requested amount must be greater than zero.");
        }
        if (termMonths == null || termMonths <= 0) {
            throw new BusinessException("Term in months must be greater than zero.");
        }

        // 3. Construir la solicitud
        Loan loan = new Loan();
        loan.setClientRequestorId(clientRequestorId);
        loan.setLoanType(loanType);
        loan.setRequestedAmount(requestedAmount);
        loan.setTermMonths(termMonths);
        loan.setDestinationAccountDisbursement(destinationAccountDisbursement);
        loan.setLoanStatus(LoanStatus.UnderReview);

        loanPort.save(loan);

        // 4. Registrar en Bitácora
        recordLoanStatusChange(loan, "LOAN_REQUESTED", null, LoanStatus.UnderReview,
            creatorUserId, creatorRole, null, null);

        return loan;
    }


    // Aprobación / Rechazo (Analista Interno)

    public Loan approveLoan(Long loanId,
                             BigDecimal approvedAmount,
                             BigDecimal interestRate,
                             Long analystId,
                             String analystRole) {

        Loan loan = getLoanById(loanId);

        // Validar transición de estado
        if (loan.getLoanStatus() != LoanStatus.UnderReview) {
            throw new BusinessException(
                "Loan " + loanId + " cannot be approved. Current status: "
                    + loan.getLoanStatus() + ". Only loans in 'UnderReview' can be approved.");
        }

        // Validar monto aprobado
        if (approvedAmount == null || approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Approved amount must be greater than zero.");
        }

        LoanStatus previousStatus = loan.getLoanStatus();

        // Actualizar préstamo
        loan.setLoanStatus(LoanStatus.Approved);
        loan.setApprovedAmount(approvedAmount);
        loan.setInterestRate(interestRate);
        loan.setApprovalDate(LocalDate.now());
        loan.setApproverAnalystId(analystId);

        loanPort.update(loan);

        // Registrar en Bitácora
        recordLoanStatusChange(loan, "LOAN_APPROVED", previousStatus, LoanStatus.Approved,
            analystId, analystRole, approvedAmount, interestRate);

        return loan;
    }

    /**
     * Rechazar un préstamo. (Solo Analista Interno)
     */
    public Loan rejectLoan(Long loanId, Long analystId, String analystRole) {

        Loan loan = getLoanById(loanId);

        if (loan.getLoanStatus() != LoanStatus.UnderReview) {
            throw new BusinessException(
                "Loan " + loanId + " cannot be rejected. Current status: "
                    + loan.getLoanStatus() + ". Only loans in 'UnderReview' can be rejected.");
        }

        LoanStatus previousStatus = loan.getLoanStatus();
        loan.setLoanStatus(LoanStatus.Rejected);
        loan.setApprovalDate(LocalDate.now());
        loan.setApproverAnalystId(analystId);

        loanPort.update(loan);

        recordLoanStatusChange(loan, "LOAN_REJECTED", previousStatus, LoanStatus.Rejected,
            analystId, analystRole, null, null);

        return loan;
    }


    // Desembolso (solo Analista Interno)

    public Loan disburseLoan(Long loanId, Long analystId, String analystRole) {

        Loan loan = getLoanById(loanId);

        // 1. Validar estado
        if (loan.getLoanStatus() != LoanStatus.Approved) {
            throw new BusinessException(
                "Loan " + loanId + " cannot be disbursed. Current status: "
                    + loan.getLoanStatus() + ". Only 'Approved' loans can be disbursed.");
        }

        // 2. Validar que la cuenta destino esté definida
        if (loan.getDestinationAccountDisbursement() == null
                || loan.getDestinationAccountDisbursement().isBlank()) {
            throw new BusinessException(
                "Loan " + loanId + " does not have a destination account for disbursement.");
        }

        // 3. Validar que el monto aprobado sea mayor a cero
        if (loan.getApprovedAmount() == null
                || loan.getApprovedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                "Approved amount for loan " + loanId + " must be greater than zero.");
        }

        // 4. Validar que la cuenta destino exista, esté activa y pertenezca al cliente
        BankAccount destinationAccount = bankAccountPort
            .findByAccountNumber(loan.getDestinationAccountDisbursement())
            .orElseThrow(() -> new NotFoundException(
                "Destination account " + loan.getDestinationAccountDisbursement() + " not found."));

        if (!destinationAccount.getAccountHolderId().equals(loan.getClientRequestorId())) {
            throw new BusinessException(
                "Destination account " + loan.getDestinationAccountDisbursement()
                    + " does not belong to the loan applicant " + loan.getClientRequestorId() + ".");
        }

        // 5. Calcular nuevo saldo y actualizar cuenta
        BigDecimal balanceBefore = destinationAccount.getCurrentBalance();
        BigDecimal balanceAfter = balanceBefore.add(loan.getApprovedAmount());
        bankAccountPort.updateBalance(loan.getDestinationAccountDisbursement(), balanceAfter);

        // 6. Actualizar préstamo
        LoanStatus previousStatus = loan.getLoanStatus();
        loan.setLoanStatus(LoanStatus.Disbursed);
        loan.setDisbursementDate(LocalDate.now());
        loanPort.update(loan);

        // 7. Registrar en Bitácora
        OperationsLog record = OperationsLog.builder()
            .logbookId(UUID.randomUUID().toString())
            .operationType("LOAN_DISBURSED")
            .operationDateTime(LocalDateTime.now())
            .userId(analystId)
            .userRole(analystRole)
            .affectedProductId(String.valueOf(loan.getLoanId()))
            .detailData(Map.of(
                "loanId", loan.getLoanId(),
                "clientRequestorId", loan.getClientRequestorId(),
                "previousStatus", previousStatus.name(),
                "newStatus", LoanStatus.Disbursed.name(),
                "approvedAmount", loan.getApprovedAmount(),
                "destinationAccount", loan.getDestinationAccountDisbursement(),
                "balanceBeforeDisbursement", balanceBefore,
                "balanceAfterDisbursement", balanceAfter,
                "disbursementDate", loan.getDisbursementDate().toString(),
                "analystId", analystId
            ))
            .build();

        bitacoraPort.save(record);

        return loan;
    }


    // Consultas
    /**
     * Obtiene un préstamo por su ID.
     */
    public Loan getLoanById(Long loanId) {
        return loanPort.findById(loanId)
            .orElseThrow(() -> new NotFoundException(
                "Loan with ID " + loanId + " not found."));
    }

    /**
     * Obtiene todos los préstamos de un cliente.
     */
    public List<Loan> getLoansByClient(String clientId) {
        return loanPort.findByClient(clientId);
    }

    /**
     * Obtiene todos los préstamos en estado UnderReview para revisión del Analista.
     *
     */
    public List<Loan> getPendingLoansForReview() {
        return loanPort.findByStatus(LoanStatus.UnderReview);
    }

    /**
     * Obtiene todos los préstamos del sistema (acceso del Analista Interno).
     
     */
    public List<Loan> getAllLoans() {
        return loanPort.findAll();
    }

    /**
     * Obtiene préstamos de un cliente filtrados por estado.
     * Usado por el Empleado Comercial para seguimiento.
     */
    public List<Loan> getLoansByClientAndStatus(String clientId, LoanStatus status) {
        return loanPort.findByClientAndStatus(clientId, status);
    }


    // Métodos privados

    private void recordLoanStatusChange(Loan loan,
                                                String operationType,
                                                LoanStatus previousStatus,
                                                LoanStatus newStatus,
                                                Long userId,
                                                String userRole,
                                                BigDecimal approvedAmount,
                                                BigDecimal interestRate) {
        Map<String, Object> detailData = new java.util.HashMap<>();
        detailData.put("loanId", loan.getLoanId());
        detailData.put("clientRequestorId", loan.getClientRequestorId());
        detailData.put("newStatus", newStatus.name());
        if (previousStatus != null) detailData.put("previousStatus", previousStatus.name());
        if (approvedAmount != null) detailData.put("approvedAmount", approvedAmount);
        if (interestRate != null) detailData.put("interestRate", interestRate);
        if (loan.getApproverAnalystId() != null) detailData.put("analystId", loan.getApproverAnalystId());

        OperationsLog record = OperationsLog.builder()
            .logbookId(UUID.randomUUID().toString())
            .operationType(operationType)
            .operationDateTime(LocalDateTime.now())
            .userId(userId)
            .userRole(userRole)
            .affectedProductId(loan.getLoanId() != null ? String.valueOf(loan.getLoanId()) : "PENDING")
            .detailData(detailData)
            .build();

        bitacoraPort.save(record);
    }
}
