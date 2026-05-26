package app.application.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.application.api.request.LoanRequest;
import app.application.api.response.LoanResponse;
import app.domain.models.Loan;
import app.domain.models.UserSystem;
import app.domain.models.enums.LoanStatus;
import app.domain.services.LoanService;
import app.domain.services.UserSystemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/loans")
public class LoanController {
    
    private final LoanService loanService;
    private final UserSystemService userSystemService;

    public LoanController(LoanService loanService, UserSystemService userSystemService) {
        this.loanService = loanService;
        this.userSystemService = userSystemService;
    }


    //── ANALISTA INTERNO ──────────────────────────────────────────────────────────────

    /**
     * GET /internal_analyst/loans
     * Lista todos los préstamos del sistema.
     */
    @GetMapping("/internal_analyst/loans")
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        List<LoanResponse> response = loanService.getAllLoans()
                .stream()
                .map(LoanController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/loans/pending
     * Lista todos los préstamos en estado UnderReview (pendientes de revisión).
     */
    @GetMapping("/internal_analyst/loans/pending")
    public ResponseEntity<List<LoanResponse>> getPendingLoans() {
        List<LoanResponse> response = loanService.getPendingLoansForReview()
                .stream()
                .map(LoanController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/loans/{loanId}
     * Consulta un préstamo específico por ID.
     */
    @GetMapping("/internal_analyst/loans/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(toResponse(loan));
    }

    /**
     * GET /internal_analyst/clients/{clientId}/loans
     * Lista todos los préstamos de un cliente (Cédula o NIT).
     */
    @GetMapping("/internal_analyst/clients/{clientId}/loans")
    public ResponseEntity<List<LoanResponse>> getLoansByClient(@PathVariable String clientId) {
        List<LoanResponse> response = loanService.getLoansByClient(clientId)
                .stream()
                .map(LoanController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /internal_analyst/loans/{loanId}/approve
     * Aprueba un préstamo en estado UnderReview.
     * El monto aprobado y la tasa de interés se pasan en el cuerpo.
     */
    @PutMapping("/internal_analyst/loans/{loanId}/approve")
    public ResponseEntity<LoanResponse> approveLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {

        String analystDocument = (String) authentication.getDetails();
        UserSystem analyst = userSystemService.getUserByDocument(analystDocument);

        Loan loan = loanService.approveLoan(
                loanId,
                request.getApprovedAmount(),
                request.getInterestRate(),
                analyst.getUserId(),
                analyst.getSystemRole().name()
        );
        return ResponseEntity.ok(toResponse(loan));
    }

    /**
     * PUT /internal_analyst/loans/{loanId}/reject
     * Rechaza un préstamo en estado UnderReview.
     */
    @PutMapping("/internal_analyst/loans/{loanId}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(
            @PathVariable Long loanId,
            Authentication authentication) {

        String analystDocument = (String) authentication.getDetails();
        UserSystem analyst = userSystemService.getUserByDocument(analystDocument);

        Loan loan = loanService.rejectLoan(
                loanId,
                analyst.getUserId(),
                analyst.getSystemRole().name()
        );
        return ResponseEntity.ok(toResponse(loan));
    }

    /**
     * PUT /internal_analyst/loans/{loanId}/disburse
     * Desembolsa un préstamo aprobado: abona el monto a la cuenta destino.
     */
    @PutMapping("/internal_analyst/loans/{loanId}/disburse")
    public ResponseEntity<LoanResponse> disburseLoan(
            @PathVariable Long loanId,
            Authentication authentication) {

        String analystDocument = (String) authentication.getDetails();
        UserSystem analyst = userSystemService.getUserByDocument(analystDocument);

        Loan loan = loanService.disburseLoan(
                loanId,
                analyst.getUserId(),
                analyst.getSystemRole().name()
        );
        return ResponseEntity.ok(toResponse(loan));
    }


    // ── EMPLEADO COMERCIAL ──────────────────────────────────────────────────────────────

    /**
     * POST /sales_employe/loans
     * Crea una solicitud de préstamo en nombre de un cliente.
     */
    @PostMapping("/sales_employe/loans")
    public ResponseEntity<LoanResponse> createLoanRequestBySales(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {

        String salesDocument = (String) authentication.getDetails();
        UserSystem salesEmployee = userSystemService.getUserByDocument(salesDocument);

        Loan loan = loanService.createLoanRequest(
                request.getClientRequestorId(),
                request.getLoanType(),
                request.getRequestedAmount(),
                request.getTermMonths(),
                request.getDestinationAccountDisbursement(),
                salesEmployee.getUserId(),
                salesEmployee.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(loan));
    }

    /**
     * GET /sales_employe/clients/{clientId}/loans
     * Consulta los préstamos de un cliente para seguimiento (solo UnderReview y Rejected).
     */
    @GetMapping("/sales_employe/clients/{clientId}/loans")
    public ResponseEntity<List<LoanResponse>> getLoansByClientUnderReviewOrRejected(
            @PathVariable String clientId) {
        List<LoanResponse> underReview = loanService
                .getLoansByClientAndStatus(clientId, LoanStatus.UnderReview)
                .stream().map(LoanController::toResponse).toList();

        List<LoanResponse> rejected = loanService
                .getLoansByClientAndStatus(clientId, LoanStatus.Rejected)
                .stream().map(LoanController::toResponse).toList();

        List<LoanResponse> combined = new java.util.ArrayList<>(underReview);
        combined.addAll(rejected);
        return ResponseEntity.ok(combined);
    }


    // ── CLIENTE PERSONA NATURAL ──────────────────────────────────────────────────────────────

    /**
     * POST /person_customer_user/loans
     * El cliente persona natural solicita un nuevo préstamo.
     */
    @PostMapping("/person_customer_user/loans")
    public ResponseEntity<LoanResponse> createLoanRequestByNaturalPerson(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);

        Loan loan = loanService.createLoanRequest(
                identification, 
                request.getLoanType(),
                request.getRequestedAmount(),
                request.getTermMonths(),
                request.getDestinationAccountDisbursement(),
                user.getUserId(),
                user.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(loan));
    }

    /**
     * GET /person_customer_user/loans
     * El cliente persona natural consulta todos sus préstamos.
     */
    @GetMapping("/person_customer_user/loans")
    public ResponseEntity<List<LoanResponse>> getOwnLoans(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        List<LoanResponse> response = loanService.getLoansByClient(identification)
                .stream()
                .map(LoanController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /person_customer_user/loans/{loanId}
     * El cliente consulta el detalle de un préstamo propio.
     */
    @GetMapping("/person_customer_user/loans/{loanId}")
    public ResponseEntity<LoanResponse> getOwnLoanById(
            @PathVariable Long loanId,
            Authentication authentication) {
        String identification = (String) authentication.getDetails();
        Loan loan = loanService.getLoanById(loanId);
        // Validar que el préstamo pertenezca al cliente autenticado
        if (!loan.getClientRequestorId().equals(identification)) {
            throw new app.domain.Exceptions.BusinessException(
                    "Loan " + loanId + " does not belong to the authenticated client.");
        }
        return ResponseEntity.ok(toResponse(loan));
    }


    // ── CLIENTE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * POST /corporate_customer_user/loans
     * El cliente empresa (representante legal) solicita un préstamo para la empresa.
     */
    @PostMapping("/corporate_customer_user/loans")
    public ResponseEntity<LoanResponse> createLoanRequestByCompany(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(user);

        Loan loan = loanService.createLoanRequest(
                nit,
                request.getLoanType(),
                request.getRequestedAmount(),
                request.getTermMonths(),
                request.getDestinationAccountDisbursement(),
                user.getUserId(),
                user.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(loan));
    }

    /**
     * GET /corporate_customer_user/loans
     * El cliente empresa consulta todos los préstamos de su empresa.
     */
    @GetMapping("/corporate_customer_user/loans")
    public ResponseEntity<List<LoanResponse>> getCompanyLoans(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(user);
        List<LoanResponse> response = loanService.getLoansByClient(nit)
                .stream()
                .map(LoanController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    private String resolveCompanyNit(UserSystem user) {
        if (user.getRelatedClientId() instanceof app.domain.models.CompanyClient co) {
            return co.getNit();
        }
        throw new app.domain.Exceptions.BusinessException(
                "User " + user.getIdentification() + " is not associated with a company.");
    }

    // =========================================================================
    // Mappers
    // =========================================================================

    private static LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getLoanId(),
                loan.getLoanType(),
                loan.getClientRequestorId(),
                loan.getRequestedAmount(),
                loan.getApprovedAmount(),
                loan.getInterestRate(),
                loan.getTermMonths(),
                loan.getLoanStatus(),
                loan.getApprovalDate(),
                loan.getDisbursementDate(),
                loan.getDestinationAccountDisbursement(),
                loan.getApproverAnalystId()
        );
    }

}
