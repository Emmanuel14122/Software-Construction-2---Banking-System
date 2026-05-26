package app.application.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.application.api.request.UserSystemRequest;
import app.application.api.response.UserSystemResponse;
import app.domain.models.UserSystem;
import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;
import app.domain.services.UserSystemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/system-users")
public class UserSystemController {
        private final UserSystemService userSystemService;
    private final PasswordEncoder passwordEncoder;

    public UserSystemController(UserSystemService userSystemService,
                                 PasswordEncoder passwordEncoder) {
        this.userSystemService = userSystemService;
        this.passwordEncoder = passwordEncoder;
    }


    // ── ANALISTA INTERNO ──────────────────────────────────────────────────────────────

    /**
     * GET /internal_analyst/users
     * Lista todos los usuarios del sistema.
     */
    @GetMapping("/internal_analyst/users")
    public ResponseEntity<List<UserSystemResponse>> getAllUsers() {
        List<UserSystemResponse> response = userSystemService.getAllUsers()
                .stream()
                .map(UserSystemController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/users/{identification}
     * Consulta un usuario por número de identificación.
     */
    @GetMapping("/internal_analyst/users/{identification}")
    public ResponseEntity<UserSystemResponse> getUserByDocument(@PathVariable String identification) {
        UserSystem user = userSystemService.getUserByDocument(identification);
        return ResponseEntity.ok(toResponse(user));
    }

    /**
     * GET /internal_analyst/users/by-role?role=InternalAnalyst
     * Lista usuarios filtrados por rol. Útil para auditoría y asignación de analistas.
     */
    @GetMapping("/internal_analyst/users/by-role")
    public ResponseEntity<List<UserSystemResponse>> getUsersByRole(@RequestParam UserRole role) {
        List<UserSystemResponse> response = userSystemService.getUsersByRole(role)
                .stream()
                .map(UserSystemController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /internal_analyst/users
     * Crea un nuevo usuario en el sistema (empleados internos, etc.).
     * La contraseña se cifra antes de persistir.
     */
    @PostMapping("/internal_analyst/users")
    public ResponseEntity<UserSystemResponse> createUser(
            @Valid @RequestBody UserSystemRequest request) {
        UserSystem user = toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userSystemService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    /**
     * PUT /internal_analyst/users/{identification}
     * Actualiza los datos de un usuario existente.
     */
    @PutMapping("/internal_analyst/users/{identification}")
    public ResponseEntity<UserSystemResponse> updateUser(
            @PathVariable String identification,
            @Valid @RequestBody UserSystemRequest request) {
        request.setIdentification(identification);
        UserSystem user = toModel(request);
        userSystemService.updateUser(user);
        return ResponseEntity.ok(toResponse(user));
    }

    /**
     * PUT /internal_analyst/users/{userId}/status?status=Inactive
     * Cambia el estado de un usuario (Active, Inactive, Blocked).
     */
    @PutMapping("/internal_analyst/users/{userId}/status")
    public ResponseEntity<Void> changeUserStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status) {
        userSystemService.changeUserStatus(userId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/internal_analyst/users/{identification}")
    public ResponseEntity<Void> deactivateUser(@PathVariable String identification) {
        userSystemService.deactivateUser(identification);
        return ResponseEntity.noContent().build();
    }


    // ── SUPERVISOR DE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * POST /corporate_supervisor/users
     * Crea un nuevo usuario operativo (CorporateEmployee) para la empresa.
     */
    @PostMapping("/corporate_supervisor/users")
    public ResponseEntity<UserSystemResponse> createCorporateEmployee(
            @Valid @RequestBody UserSystemRequest request,
            Authentication authentication) {

        request.setSystemRole(UserRole.CorporateEmployee);
        UserSystem user = toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userSystemService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    /**
     * GET /corporate_supervisor/users
     * Lista los usuarios operativos de la empresa.
     */
    @GetMapping("/corporate_supervisor/users")
    public ResponseEntity<List<UserSystemResponse>> getCorporateEmployees(
            Authentication authentication) {
        List<UserSystemResponse> response = userSystemService
                .getActiveUsersByRole(UserRole.CorporateEmployee)
                .stream()
                .map(UserSystemController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /corporate_supervisor/users/{userId}/status?status=Inactive
     * El Supervisor puede activar/desactivar usuarios operativos de su empresa.
     */
    @PutMapping("/corporate_supervisor/users/{userId}/status")
    public ResponseEntity<Void> changeCorporateEmployeeStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status) {
        userSystemService.changeUserStatus(userId, status);
        return ResponseEntity.noContent().build();
    }


    // ── EMPLEADO COMERCIAL ──────────────────────────────────────────────────────────────


    /**
     * POST /sales_employe/users
     * El Empleado Comercial registra un nuevo usuario cliente (PersonCustomerUser o CorporateCustomerUser).
     */
    @PostMapping("/sales_employe/users")
    public ResponseEntity<UserSystemResponse> registerClientUser(
            @Valid @RequestBody UserSystemRequest request) {
        UserSystem user = toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userSystemService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    // ── EMPLEADO DE VENTANILLA ──────────────────────────────────────────────────────────────

    /**
     * POST /window_employe/users
     * El Empleado de Ventanilla registra el acceso digital de un nuevo cliente.
     */
    @PostMapping("/window_employe/users")
    public ResponseEntity<UserSystemResponse> registerClientUserAtWindow(
            @Valid @RequestBody UserSystemRequest request) {
        UserSystem user = toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userSystemService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    // =========================================================================
    // Mappers
    // =========================================================================

    private static UserSystem toModel(UserSystemRequest request) {
        UserSystem user = new UserSystem();
        user.setUserId(request.getUserId());
        user.setFullName(request.getFullName());
        user.setIdentification(request.getIdentification());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setAddress(request.getAddress());
        user.setSystemRole(request.getSystemRole());
        user.setUserStatus(request.getUserStatus());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }

    private static UserSystemResponse toResponse(UserSystem user) {
        return new UserSystemResponse(
                user.getUserId(),
                null,
                user.getFullName(),
                user.getIdentification(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate(),
                user.getAddress(),
                user.getSystemRole(),
                user.getUserStatus(),
                user.getUsername(),
                null
        );
    }
}
