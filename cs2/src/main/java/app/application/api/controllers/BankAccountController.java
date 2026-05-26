package app.application.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.application.api.request.BankAccountRequest;
import app.application.api.response.BankAccountResponse;
import app.domain.models.BankAccount;
import app.domain.models.UserSystem;
import app.domain.services.BankAccountService;
import app.domain.services.UserSystemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bank-accounts")
public class BankAccountController {
    
    private final BankAccountService bankAccountService;
    private final UserSystemService userSystemService;

    public BankAccountController(BankAccountService bankAccountService,
                                  UserSystemService userSystemService) {
        this.bankAccountService = bankAccountService;
        this.userSystemService = userSystemService;
    }


    //  ── ANALISTA INTERNO ──────────────────────────────────────────────────────────────

    /**
     * GET /internal_analyst/accounts
     * Lista todas las cuentas del sistema.
     */
    @GetMapping("/internal_analyst/accounts")
    public ResponseEntity<List<BankAccountResponse>> getAllAccounts() {
        List<BankAccountResponse> response = bankAccountService.getAllAccounts()
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/accounts/{accountNumber}
     * Consulta una cuenta específica por número.
     */
    @GetMapping("/internal_analyst/accounts/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getAccountByNumber(
            @PathVariable String accountNumber) {
        BankAccount account = bankAccountService.getByAccountNumber(accountNumber);
        return ResponseEntity.ok(toResponse(account));
    }

    /**
     * GET /internal_analyst/clients/{clientId}/accounts
     * Lista todas las cuentas de un cliente (Cédula o NIT).
     */
    @GetMapping("/internal_analyst/clients/{clientId}/accounts")
    public ResponseEntity<List<BankAccountResponse>> getAccountsByClient(
            @PathVariable String clientId) {
        List<BankAccountResponse> response = bankAccountService.getAccountsByTitular(clientId)
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // ── EMPLEADO DE VENTANILLA ──────────────────────────────────────────────────────

    /**
     * POST /window_employe/accounts
     * Abre una nueva cuenta bancaria para un cliente.
     */
    @PostMapping("/window_employe/accounts")
    public ResponseEntity<BankAccountResponse> openAccountAtWindow(
            @Valid @RequestBody BankAccountRequest request,
            Authentication authentication) {

        String operatorDocument = (String) authentication.getDetails();
        UserSystem operator = userSystemService.getUserByDocument(operatorDocument);

        BankAccount account = bankAccountService.openAccount(
                request.getAccountHolderId(),
                request.getAccountNumber(),
                request.getAccountType(),
                request.getCurrency(),
                request.getCurrentBalance(),
                operator.getUserId(),
                operator.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(account));
    }

    /**
     * GET /window_employe/accounts/{accountNumber}
     * Consulta saldo y estado de una cuenta para operaciones de caja.
     */
    @GetMapping("/window_employe/accounts/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getAccountAtWindow(
            @PathVariable String accountNumber) {
        BankAccount account = bankAccountService.getByAccountNumber(accountNumber);
        return ResponseEntity.ok(toResponse(account));
    }

    /**
     * GET /window_employe/clients/{clientId}/accounts
     * Lista todas las cuentas de un cliente (para identificar al cliente en ventanilla).
     */
    @GetMapping("/window_employe/clients/{clientId}/accounts")
    public ResponseEntity<List<BankAccountResponse>> getClientAccountsAtWindow(
            @PathVariable String clientId) {
        List<BankAccountResponse> response = bankAccountService.getAccountsByTitular(clientId)
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // ── EMPLEADO COMERCIAL ──────────────────────────────────────────────────────────────

    /**
     * POST /sales_employe/accounts
     * Abre una cuenta bancaria en nombre de un cliente gestionado.
     */
    @PostMapping("/sales_employe/accounts")
    public ResponseEntity<BankAccountResponse> openAccountForClient(
            @Valid @RequestBody BankAccountRequest request,
            Authentication authentication) {

        String operatorDocument = (String) authentication.getDetails();
        UserSystem operator = userSystemService.getUserByDocument(operatorDocument);

        BankAccount account = bankAccountService.openAccount(
                request.getAccountHolderId(),
                request.getAccountNumber(),
                request.getAccountType(),
                request.getCurrency(),
                request.getCurrentBalance(),
                operator.getUserId(),
                operator.getSystemRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(account));
    }


    // ── CLIENTE PERSONA NATURAL ──────────────────────────────────────────────────────────────

    /**
     * GET /person_customer_user/accounts
     * El cliente persona natural consulta todas sus cuentas.
     */
    @GetMapping("/person_customer_user/accounts")
    public ResponseEntity<List<BankAccountResponse>> getOwnAccounts(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        List<BankAccountResponse> response = bankAccountService.getAccountsByTitular(identification)
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /person_customer_user/accounts/{accountNumber}
     * El cliente persona natural consulta una cuenta propia específica.
     */
    @GetMapping("/person_customer_user/accounts/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getOwnAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {
        String identification = (String) authentication.getDetails();
        BankAccount account = bankAccountService.validateAccountBelongsToClient(accountNumber, identification);
        return ResponseEntity.ok(toResponse(account));
    }


    // ── CLIENTE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * GET /corporate_customer_user/accounts
     * El cliente empresa consulta todas las cuentas de su empresa.
     */
    @GetMapping("/corporate_customer_user/accounts")
    public ResponseEntity<List<BankAccountResponse>> getCompanyAccounts(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(user);
        List<BankAccountResponse> response = bankAccountService.getAccountsByTitular(nit)
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    /**
     * GET /corporate_employe/accounts
     * El empleado de empresa consulta las cuentas de su empresa.
     */
    @GetMapping("/corporate_employe/accounts")
    public ResponseEntity<List<BankAccountResponse>> getCompanyAccountsForEmployee(
            Authentication authentication) {
        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(user);
        List<BankAccountResponse> response = bankAccountService.getAccountsByTitular(nit)
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    /**
     * GET /corporate_supervisor/accounts
     * El supervisor consulta las cuentas de su empresa.
     */
    @GetMapping("/corporate_supervisor/accounts")
    public ResponseEntity<List<BankAccountResponse>> getCompanyAccountsForSupervisor(
            Authentication authentication) {
        String identification = (String) authentication.getDetails();
        UserSystem user = userSystemService.getUserByDocument(identification);
        String nit = resolveCompanyNit(user);
        List<BankAccountResponse> response = bankAccountService.getAccountsByTitular(nit)
                .stream()
                .map(BankAccountController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Extrae el NIT de la empresa del campo relatedClientId del usuario.
     * Aplica a usuarios corporativos (empleado, supervisor, cliente empresa).
     */
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

    private static BankAccountResponse toResponse(BankAccount account) {
        return new BankAccountResponse(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getAccountHolderId(),
                account.getCurrentBalance(),
                account.getCurrency(),
                account.getAccountStatus(),
                account.getOpeningDate()
        );
    }
}