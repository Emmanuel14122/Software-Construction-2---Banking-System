package app.domain.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.NotFoundException;
import app.domain.models.BankAccount;
import app.domain.models.OperationsLog;
import app.domain.models.Transfer;
import app.domain.models.enums.AccountStatus;
import app.domain.models.enums.TransferStatus;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import app.domain.ports.TransferPort;


public class TransferService {


    // Umbral de monto para transferencias empresariales que requieren aprobación.
    public static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000000");

    //Tiempo máximo en minutos para que el Supervisor apruebe una transferencia
    public static final long APPROVAL_TIMEOUT_MINUTES = 60L;

    private final TransferPort transferPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort bitacoraPort;

    public TransferService(TransferPort transferPort,
                           BankAccountPort bankAccountPort,
                           OperationsLogPort bitacoraPort) {
        this.transferPort = transferPort;
        this.bankAccountPort = bankAccountPort;
        this.bitacoraPort = bitacoraPort;
    }

    // Creación de transferencia (persona natural)
    public Transfer createAndExecutePersonalTransfer(String originAccount,
                                                      String destinationAccount,
                                                      BigDecimal amount,
                                                      Long creatorUserId,
                                                      String creatorRole) {

        validateTransferBasicRules(amount, originAccount, destinationAccount);

        BankAccount origin = validateOriginAccountHasFunds(originAccount, amount);
        BankAccount destination = getDestinationAccount(destinationAccount);

        Transfer transfer = buildTransfer(originAccount, destinationAccount, amount,
            creatorUserId, TransferStatus.Executed, null);

        transferPort.save(transfer);

        executeTransferFunds(transfer, origin, destination, creatorUserId, creatorRole);

        return transfer;
    }

    // Creación de transferencia (empresa)
    public Transfer createCompanyTransfer(String originAccount,
                                           String destinationAccount,
                                           BigDecimal amount,
                                           Long creatorUserId,
                                           String creatorRole) {

        validateTransferBasicRules(amount, originAccount, destinationAccount);

        boolean requiresApproval = amount.compareTo(HIGH_VALUE_THRESHOLD) > 0;

        if (requiresApproval) {
            // Crear en WaitingApproval con tiempo de expiración
            Transfer transfer = buildTransfer(originAccount, destinationAccount, amount,
                creatorUserId, TransferStatus.WaitingApproval,
                LocalDateTime.now().plusMinutes(APPROVAL_TIMEOUT_MINUTES));

            transferPort.save(transfer);

            // Registrar en Bitácora: creación con solicitud de aprobación
            registerTransfer(transfer, "TRANSFER_AWAITING_APPROVAL", null, null,
                null, null, creatorUserId, creatorRole);

            return transfer;
        } else {
            // Ejecutar directamente
            validateOriginAccountOperational(originAccount);
            BankAccount origin = validateOriginAccountHasFunds(originAccount, amount);
            BankAccount destination = getDestinationAccount(destinationAccount);

            Transfer transfer = buildTransfer(originAccount, destinationAccount, amount,
                creatorUserId, TransferStatus.Executed, null);

            transferPort.save(transfer);
            executeTransferFunds(transfer, origin, destination, creatorUserId, creatorRole);

            return transfer;
        }
    }


    // Aprobación / Rechazo (solo Supervisor de Empresa)
    public Transfer approveTransfer(Long transferId, Long supervisorId, String supervisorRole) {

        Transfer transfer = getTransferById(transferId);

        // Validar estado
        if (transfer.getTransferStatus() != TransferStatus.WaitingApproval) {
            throw new BusinessException(
                "Transfer " + transferId + " cannot be approved. Current status: "
                    + transfer.getTransferStatus() + ".");
        }

        // Verificar que no haya vencido
        if (transfer.getExpirationCheckAt() != null
                && LocalDateTime.now().isAfter(transfer.getExpirationCheckAt())) {
            throw new BusinessException(
                "Transfer " + transferId + " has expired and can no longer be approved.");
        }

        // Validar fondos en el momento de aprobación
        BankAccount origin = validateOriginAccountHasFunds(
            transfer.getOriginAccount(), transfer.getAmount());
        BankAccount destination = getDestinationAccount(transfer.getDestinationAccount());

        // Actualizar transferencia
        transfer.setTransferStatus(TransferStatus.Approved);
        transfer.setApprovalDate(LocalDateTime.now());
        transfer.setApproverUserId(supervisorId);
        transferPort.update(transfer);

        // Ejecutar movimiento de fondos
        executeTransferFunds(transfer, origin, destination, supervisorId, supervisorRole);

        return transfer;
    }

    //Rechaza una transferencia en estado WaitingApproval.(solo Supervisor de Empresa)
    public Transfer rejectTransfer(Long transferId, Long supervisorId, String supervisorRole) {

        Transfer transfer = getTransferById(transferId);

        if (transfer.getTransferStatus() != TransferStatus.WaitingApproval) {
            throw new BusinessException(
                "Transfer " + transferId + " cannot be rejected. Current status: "
                    + transfer.getTransferStatus() + ".");
        }

        transfer.setTransferStatus(TransferStatus.Rejected);
        transfer.setApprovalDate(LocalDateTime.now());
        transfer.setApproverUserId(supervisorId);
        transferPort.update(transfer);

        registerTransfer(transfer, "TRANSFER_REJECTED", null, null,
            null, null, supervisorId, supervisorRole);

        return transfer;
    }


    // Vencimiento automático
    public void processExpiredTransfers() {
        LocalDateTime now = LocalDateTime.now();
        List<Transfer> expiredTransfers = transferPort.findWaitingApprovalExpiredBefore(now);

        for (Transfer transfer : expiredTransfers) {
            transfer.setTransferStatus(TransferStatus.Expired);
            transferPort.update(transfer);

            // Registrar vencimiento en Bitácora (requerido explícitamente por el enunciado)
            OperationsLog record = OperationsLog.builder()
                .logbookId(UUID.randomUUID().toString())
                .operationType("TRANSFER_EXPIRED")
                .operationDateTime(now)
                .userId(0L) // usuario SISTEMA (job automático)
                .userRole("SYSTEM_JOB")
                .affectedProductId(String.valueOf(transfer.getTransferId()))
                .detailData(Map.of(
                    "transferId", transfer.getTransferId(),
                    "originAccount", transfer.getOriginAccount(),
                    "destinationAccount", transfer.getDestinationAccount(),
                    "amount", transfer.getAmount(),
                    "createdAt", transfer.getCreationDate().toString(),
                    "expiredAt", now.toString(),
                    "creatorUserId", transfer.getCreatorUserId(),
                    "expirationReason", "Falta de aprobación a tiempo"
                ))
                .build();

            bitacoraPort.save(record);
        }
    }

    // Consultas
    /**
     * Obtiene una transferencia por su ID.
     */
    public Transfer getTransferById(Long transferId) {
        return transferPort.findById(transferId)
            .orElseThrow(() -> new NotFoundException(
                "Transfer with ID " + transferId + " not found."));
    }

    //Obtiene todas las transferencias de una cuenta (origen o destino).
    public List<Transfer> getTransfersByAccount(String accountNumber) {
        return transferPort.findByAccount(accountNumber);
    }

    /**
     * Obtiene las transferencias en WaitingApproval de una empresa.
     * Para uso del Supervisor de Empresa.
     */
    public List<Transfer> getPendingApprovalByCompany(String companyNit) {
        return transferPort.findByCompanyAndStatus(companyNit, TransferStatus.WaitingApproval);
    }

    //Obtiene las transferencias creadas por un usuario específico.
    public List<Transfer> getTransfersByCreator(Long creatorUserId) {
        return transferPort.findByCreatorUser(creatorUserId);
    }

    //Obtiene todas las transferencias del sistema (Analista Interno).
    public List<Transfer> getAllTransfers() {
        return transferPort.findAll();
    }


    // Métodos privados de lógica y validación
    private void validateTransferBasicRules(BigDecimal amount, String originAccount, String destinationAccount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transfer amount must be strictly greater than zero.");
        }
        if (originAccount == null || originAccount.isBlank()) {
            throw new BusinessException("Origin account is required.");
        }
        if (destinationAccount == null || destinationAccount.isBlank()) {
            throw new BusinessException("Destination account is required.");
        }
        if (originAccount.equals(destinationAccount)) {
            throw new BusinessException("Origin and destination accounts must be different.");
        }
    }

    //Valida que la cuenta origen exista y esté operacional (no Bloqueada/Cancelada).
    private void validateOriginAccountOperational(String originAccount) {
        BankAccount account = bankAccountPort.findByAccountNumber(originAccount)
            .orElseThrow(() -> new NotFoundException(
                "Origin account " + originAccount + " not found."));

        if (account.getAccountStatus() == AccountStatus.Blocked
                || account.getAccountStatus() == AccountStatus.Cancelled) {
            throw new BusinessException(
                "Origin account " + originAccount + " is " + account.getAccountStatus()
                    + ". Transfers are not allowed from blocked or cancelled accounts.");
        }
    }

    //Valida que la cuenta origen exista, esté activa y tenga fondos suficientes.
    private BankAccount validateOriginAccountHasFunds(String originAccount, BigDecimal amount) {
        BankAccount account = bankAccountPort.findByAccountNumber(originAccount)
            .orElseThrow(() -> new NotFoundException(
                "Origin account " + originAccount + " not found."));

        if (account.getAccountStatus() == AccountStatus.Blocked
                || account.getAccountStatus() == AccountStatus.Cancelled) {
            throw new BusinessException(
                "Origin account " + originAccount + " is " + account.getAccountStatus()
                    + ". Transfers are not allowed.");
        }

        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new BusinessException(
                "Insufficient funds in origin account " + originAccount
                    + ". Available: " + account.getCurrentBalance()
                    + ", Required: " + amount + ".");
        }

        return account;
    }

    //Obtiene la cuenta destino validando que exista.
    private BankAccount getDestinationAccount(String destinationAccount) {
        return bankAccountPort.findByAccountNumber(destinationAccount)
            .orElseThrow(() -> new NotFoundException(
                "Destination account " + destinationAccount + " not found."));
    }

    //Construye un objeto Transfer con los campos comunes.
    private Transfer buildTransfer(String originAccount,
                                    String destinationAccount,
                                    BigDecimal amount,
                                    Long creatorUserId,
                                    TransferStatus status,
                                    LocalDateTime expirationCheckAt) {
        Transfer transfer = new Transfer();
        transfer.setOriginAccount(originAccount);
        transfer.setDestinationAccount(destinationAccount);
        transfer.setAmount(amount);
        transfer.setCreationDate(LocalDateTime.now());
        transfer.setTransferStatus(status);
        transfer.setCreatorUserId(creatorUserId);
        transfer.setExpirationCheckAt(expirationCheckAt);
        return transfer;
    }

    //Ejecuta el movimiento de fondos entre cuentas y registra en bitácora.
    private void executeTransferFunds(Transfer transfer,
                                       BankAccount origin,
                                       BankAccount destination,
                                       Long userId,
                                       String userRole) {
        BigDecimal originBalanceBefore = origin.getCurrentBalance();
        BigDecimal destinationBalanceBefore = destination.getCurrentBalance();

        BigDecimal originBalanceAfter = originBalanceBefore.subtract(transfer.getAmount());
        BigDecimal destinationBalanceAfter = destinationBalanceBefore.add(transfer.getAmount());

        // Actualizar saldos en la BD relacional
        bankAccountPort.updateBalance(transfer.getOriginAccount(), originBalanceAfter);
        bankAccountPort.updateBalance(transfer.getDestinationAccount(), destinationBalanceAfter);

        // Marcar como Executed si no lo estaba
        if (transfer.getTransferStatus() != TransferStatus.Executed) {
            transfer.setTransferStatus(TransferStatus.Executed);
            transferPort.update(transfer);
        }

        // Registrar en Bitácora con todos los saldos antes/después
        registerTransfer(transfer, "TRANSFER_EXECUTED",
            originBalanceBefore, originBalanceAfter,
            destinationBalanceBefore, destinationBalanceAfter,
            userId, userRole);
    }

    //Registra un evento de transferencia en la Bitácora NoSQL.
    private void registerTransfer(Transfer transfer,
                                         String operationType,
                                         BigDecimal saldoAntesOrigen,
                                         BigDecimal saldoDespuesOrigen,
                                         BigDecimal saldoAntesDest,
                                         BigDecimal saldoDespuesDest,
                                         Long userId,
                                         String userRole) {
        Map<String, Object> detailData = new java.util.HashMap<>();
        detailData.put("transferId", transfer.getTransferId());
        detailData.put("originAccount", transfer.getOriginAccount());
        detailData.put("destinationAccount", transfer.getDestinationAccount());
        detailData.put("amount", transfer.getAmount());
        detailData.put("status", transfer.getTransferStatus().name());

        if (saldoAntesOrigen != null) {
            detailData.put("saldoAntesOrigen", saldoAntesOrigen);
            detailData.put("saldoDespuesOrigen", saldoDespuesOrigen);
            detailData.put("saldoAntesDest", saldoAntesDest);
            detailData.put("saldoDespuesDest", saldoDespuesDest);
        }

        OperationsLog record = OperationsLog.builder()
            .logbookId(UUID.randomUUID().toString())
            .operationType(operationType)
            .operationDateTime(LocalDateTime.now())
            .userId(userId)
            .userRole(userRole)
            .affectedProductId(String.valueOf(transfer.getTransferId()))
            .detailData(detailData)
            .build();

        bitacoraPort.save(record);
    }
}
