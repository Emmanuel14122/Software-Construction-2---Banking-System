package app.application.api.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.application.api.response.OperationsLogResponse;
import app.domain.models.OperationsLog;
import app.domain.services.OperationsLogService;

@RestController
@RequestMapping("/operations-log")
public class OperationsLogController {
    
    private final OperationsLogService operationsLogService;

    public OperationsLogController(OperationsLogService operationsLogService) {
        this.operationsLogService = operationsLogService;
    }

    // ── ANALISTA INTERNO ──────────────────────────────────────────────────────────────

    /**
     * GET /internal_analyst/operations-log
     * Lista todos los registros de la Bitácora.
     */
    @GetMapping("/internal_analyst/operations-log")
    public ResponseEntity<List<OperationsLogResponse>> getAllLogs() {
        List<OperationsLogResponse> response = operationsLogService.getAll()
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/operations-log/{logId}
     * Consulta un registro específico de la bitácora por su ID.
     */
    @GetMapping("/internal_analyst/operations-log/{logId}")
    public ResponseEntity<OperationsLogResponse> getLogById(@PathVariable String logId) {
        OperationsLog log = operationsLogService.getById(logId);
        return ResponseEntity.ok(toResponse(log));
    }

    /**
     * GET /internal_analyst/operations-log/by-product/{productId}
     * Filtra registros por ID del producto afectado (cuenta, préstamo, transferencia).
     */
    @GetMapping("/internal_analyst/operations-log/by-product/{productId}")
    public ResponseEntity<List<OperationsLogResponse>> getLogsByProduct(
            @PathVariable String productId) {
        List<OperationsLogResponse> response = operationsLogService.getByAffectedProduct(productId)
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/operations-log/by-user/{userId}
     * Filtra registros por usuario que ejecutó la acción. Para auditoría de personal.
     */
    @GetMapping("/internal_analyst/operations-log/by-user/{userId}")
    public ResponseEntity<List<OperationsLogResponse>> getLogsByUser(@PathVariable Long userId) {
        List<OperationsLogResponse> response = operationsLogService.getByUser(userId)
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/operations-log/by-type?operationType=TRANSFER_EXECUTED
     * Filtra registros por tipo de operación.
     * Tipos relevantes: LOAN_APPROVED, LOAN_DISBURSED, LOAN_REJECTED,
     *                   TRANSFER_EXECUTED, TRANSFER_EXPIRED, ACCOUNT_OPENED.
     */
    @GetMapping("/internal_analyst/operations-log/by-type")
    public ResponseEntity<List<OperationsLogResponse>> getLogsByOperationType(
            @RequestParam String operationType) {
        List<OperationsLogResponse> response = operationsLogService.getByOperationType(operationType)
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/operations-log/by-date?from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
     * Filtra registros dentro de un rango de fechas para auditorías por período.
     */
    @GetMapping("/internal_analyst/operations-log/by-date")
    public ResponseEntity<List<OperationsLogResponse>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<OperationsLogResponse> response = operationsLogService.getByDateRange(from, to)
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // ── CLIENTE PERSONA NATURAL ──────────────────────────────────────────────────────

    /**
     * GET /person_customer_user/operations-log/by-product/{productId}
     * El cliente persona natural consulta el historial de un producto propio.
     * productId puede ser: número de cuenta, ID de préstamo o ID de transferencia.
     */
    @GetMapping("/person_customer_user/operations-log/by-product/{productId}")
    public ResponseEntity<List<OperationsLogResponse>> getOwnLogsByProduct(
            @PathVariable String productId) {
        List<OperationsLogResponse> response = operationsLogService.getByAffectedProduct(productId)
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // ── CLIENTE EMPRESA ──────────────────────────────────────────────────────

    /**
     * GET /corporate_customer_user/operations-log/by-product/{productId}
     * El cliente empresa consulta el historial de un producto de la empresa.
     */
    @GetMapping("/corporate_customer_user/operations-log/by-product/{productId}")
    public ResponseEntity<List<OperationsLogResponse>> getCompanyLogsByProduct(
            @PathVariable String productId,
            Authentication authentication) {
        List<OperationsLogResponse> response = operationsLogService.getByAffectedProduct(productId)
                .stream()
                .map(OperationsLogController::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // Mappers
    // =========================================================================

    private static OperationsLogResponse toResponse(OperationsLog log) {
        return new OperationsLogResponse(
                log.getLogbookId(),
                log.getOperationType(),
                log.getOperationDateTime(),
                log.getUserId(),
                log.getUserRole(),
                log.getAffectedProductId(),
                log.getDetailData()
        );
    }
}
