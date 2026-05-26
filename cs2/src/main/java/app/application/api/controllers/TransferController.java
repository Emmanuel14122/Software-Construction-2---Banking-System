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

import app.application.api.request.TransferRequest;
import app.application.api.response.TransferResponse;
import app.domain.models.Transfer;
import app.domain.models.UserSystem;
import app.domain.services.BankAccountService;
import app.domain.services.TransferService;
import app.domain.services.UserSystemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    
    private final TransferService transferService;
    private final UserSystemService userSystemService;
    private final BankAccountService bankAccountService;

    public TransferController(TransferService transferService,
                               UserSystemService userSystemService,
                               BankAccountService bankAccountService) {
        this.transferService = transferService;
        this.userSystemService = userSystemService;
        this.bankAccountService = bankAccountService;
    }


    // ── ANALISTA INTERNO ──────────────────────────────────────────────────────────────

    /**
     * GET /internal_analyst/transfers
     * Lista todas las transferencias del sistema para auditoría.
     */
    @GetMapping("/internal_analyst/transfers")
    public ResponseEntity<List<TransferResponse>> getAllTransfers() {
        List<TransferResponse> response = transferService.getAllTransfers()
                .stream()
                .map(TransferController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/transfers/{transferId}
     * Consulta una transferencia específica por ID.
     */
    @GetMapping("/internal_analyst/transfers/{transferId}")
    public ResponseEntity<TransferResponse> getTransferById(@PathVariable Long transferId) {
        Transfer transfer = transferService.getTransferById(transferId);
        return ResponseEntity.ok(toResponse(transfer));
    }

    /**
     * GET /internal_analyst/accounts/{accountNumber}/transfers
     * Lista todas las transferencias de una cuenta (origen o destino).
     */
    @GetMapping("/internal_analyst/accounts/{accountNumber}/transfers")
    public ResponseEntity<List<TransferResponse>> getTransfersByAccount(
            @PathVariable String accountNumber) {
        List<TransferResponse> response = transferService.getTransfersByAccount(accountNumber)
                .stream()
                .map(TransferController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // ── CLIENTE PERSONA NATURAL ──────────────────────────────────────────────────────────────

    /**
     * POST /person_customer_user/transfers
     * Crea y ejecuta inmediatamente una transferencia personal.
     * Las transferencias de personas naturales no pasan por aprobación.
     */
    @PostMapping("/person_customer_user/transfers")
    public ResponseEntity<TransferResponse> createPersonalTransfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);

        Transfer transfer = transferService.createAndExecutePersonalTransfer(
                request.getOriginAccount(),
                request.getDestinationAccount(),
                request.getAmount(),
                user.getUserId(),
                user.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transfer));
    }

    /**
     * GET /person_customer_user/accounts/{accountNumber}/transfers
     * El cliente consulta el historial de transferencias de una cuenta propia.
     */
        @GetMapping("/person_customer_user/accounts/{accountNumber}/transfers")
        public ResponseEntity<List<TransferResponse>> getOwnTransfersByAccount(
                        @PathVariable String accountNumber,
                        Authentication authentication) {
                String identification = (String) authentication.getDetails();
                bankAccountService.validateAccountBelongsToClient(accountNumber, identification);

                List<TransferResponse> response = transferService.getTransfersByAccount(accountNumber)
                .stream()
                .map(TransferController::toResponse)
                .toList();
                return ResponseEntity.ok(response);
        }


    // ── EMPLEADO DE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * POST /corporate_employe/transfers
     * Crea una transferencia empresarial.
     */
    @PostMapping("/corporate_employe/transfers")
    public ResponseEntity<TransferResponse> createCompanyTransfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem employee = userSystemService.getUserByDocument(identification);

        Transfer transfer = transferService.createCompanyTransfer(
                request.getOriginAccount(),
                request.getDestinationAccount(),
                request.getAmount(),
                employee.getUserId(),
                employee.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transfer));
    }

    /**
     * GET /corporate_employe/transfers
     * El empleado de empresa consulta las transferencias creadas por él.
     */
    @GetMapping("/corporate_employe/transfers")
    public ResponseEntity<List<TransferResponse>> getOwnCreatedTransfers(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        UserSystem employee = userSystemService.getUserByDocument(identification);

        List<TransferResponse> response = transferService.getTransfersByCreator(employee.getUserId())
                .stream()
                .map(TransferController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // ── SUPERVISOR DE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * GET /corporate_supervisor/transfers/pending
     * Lista todas las transferencias de la empresa en WaitingApproval.
     * Estas son las que el Supervisor debe revisar y aprobar o rechazar.
     */
    @GetMapping("/corporate_supervisor/transfers/pending")
    public ResponseEntity<List<TransferResponse>> getPendingTransfersForApproval(
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem supervisor = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(supervisor);

        List<TransferResponse> response = transferService.getPendingApprovalByCompany(nit)
                .stream()
                .map(TransferController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /corporate_supervisor/transfers
     * El supervisor consulta todas las transferencias de su empresa.
     */
    @GetMapping("/corporate_supervisor/transfers")
    public ResponseEntity<List<TransferResponse>> getAllCompanyTransfers(
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem supervisor = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(supervisor);

        List<TransferResponse> response = transferService.getPendingApprovalByCompany(nit)
                .stream()
                .map(TransferController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /corporate_supervisor/transfers/{transferId}/approve
     * Aprueba una transferencia en WaitingApproval.
     * Valida fondos suficientes en cuenta origen y ejecuta el movimiento de dinero.
     */
    @PutMapping("/corporate_supervisor/transfers/{transferId}/approve")
    public ResponseEntity<TransferResponse> approveTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem supervisor = userSystemService.getUserByDocument(identification);

        Transfer transfer = transferService.approveTransfer(
                transferId,
                supervisor.getUserId(),
                supervisor.getSystemRole().name()
        );
        return ResponseEntity.ok(toResponse(transfer));
    }

    /**
     * PUT /corporate_supervisor/transfers/{transferId}/reject
     * Rechaza una transferencia en WaitingApproval.
     */
    @PutMapping("/corporate_supervisor/transfers/{transferId}/reject")
    public ResponseEntity<TransferResponse> rejectTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {

        String identification = (String) authentication.getDetails();
        UserSystem supervisor = userSystemService.getUserByDocument(identification);

        Transfer transfer = transferService.rejectTransfer(
                transferId,
                supervisor.getUserId(),
                supervisor.getSystemRole().name()
        );
        return ResponseEntity.ok(toResponse(transfer));
    }


    // ── CLIENTE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * GET /corporate_customer_user/accounts/{accountNumber}/transfers
     * El cliente empresa consulta el historial de transferencias de una cuenta de la empresa.
     */
    @GetMapping("/corporate_customer_user/accounts/{accountNumber}/transfers")
    public ResponseEntity<List<TransferResponse>> getCompanyAccountTransfers(
            @PathVariable String accountNumber) {
        List<TransferResponse> response = transferService.getTransfersByAccount(accountNumber)
                .stream()
                .map(TransferController::toResponse)
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

    private static TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getTransferId(),
                transfer.getOriginAccount(),
                transfer.getDestinationAccount(),
                transfer.getAmount(),
                transfer.getCreationDate(),
                transfer.getApprovalDate(),
                transfer.getTransferStatus(),
                transfer.getCreatorUserId(),
                transfer.getApproverUserId(),
                transfer.getExpirationCheckAt()
        );
    } 
}
