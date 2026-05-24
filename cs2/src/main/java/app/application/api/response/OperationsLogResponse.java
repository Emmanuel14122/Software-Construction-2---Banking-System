package app.application.api.response;
 
import java.time.LocalDateTime;
import java.util.Map;

public record OperationsLogResponse(
        String logbookId,
        String operationType,
        LocalDateTime operationDateTime,
        Long userId,
        String userRole,
        String affectedProductId,
        Map<String, Object> detailData
) {}