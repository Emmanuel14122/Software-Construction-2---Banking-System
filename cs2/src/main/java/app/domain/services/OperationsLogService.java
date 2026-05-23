package app.domain.services;

import java.time.LocalDateTime;
import java.util.List;

import app.domain.Exceptions.NotFoundException;
import app.domain.models.OperationsLog;
import app.domain.ports.OperationsLogPort;

/**
 * Servicio de dominio para la consulta de la Bitácora de Operaciones.
 */
public class OperationsLogService {

    private final OperationsLogPort operationsLogPort;

    public OperationsLogService(OperationsLogPort operationsLogPort) {
        this.operationsLogPort = operationsLogPort;
    }

    /**
     * Obtiene un registro de bitácora por su ID.
     */
    public OperationsLog getById(String logbookId) {
        return operationsLogPort.findById(logbookId)
            .orElseThrow(() -> new NotFoundException(
                "Operations log record " + logbookId + " not found."));
    }

    /**
     * Obtiene todos los registros de la bitácora relacionados con un producto.
     * Usado por Clientes para ver el historial de sus cuentas/préstamos/transferencias.
     */
    public List<OperationsLog> getByAffectedProduct(String affectedProductId) {
        return operationsLogPort.findByAffectedProduct(affectedProductId);
    }

    /**
     * Obtiene todos los registros generados por un usuario.
     * Acceso del Analista Interno para auditar acciones de un usuario específico.
     */
    public List<OperationsLog> getByUser(Long userId) {
        return operationsLogPort.findByUser(userId);
    }

    /**
     * Obtiene registros filtrados por tipo de operación.
     */
    public List<OperationsLog> getByOperationType(String operationType) {
        return operationsLogPort.findByOperationType(operationType);
    }

    /**
     * Obtiene registros dentro de un rango de fechas.
     * Acceso del Analista Interno para auditorías por período.
     */
    public List<OperationsLog> getByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                "Start date cannot be after end date.");
        }
        return operationsLogPort.findByDateRange(from, to);
    }

    /**
     * Obtiene todos los registros de la bitácora.
     * Acceso exclusivo del Analista Interno.
     */
    public List<OperationsLog> getAll() {
        return operationsLogPort.findAll();
    }
}
