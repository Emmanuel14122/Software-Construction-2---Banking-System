package app.application.api.request;
 
import lombok.Getter;
import lombok.Setter;
 
import java.util.Map;
import java.time.LocalDateTime;
 
@Getter
@Setter
public class OperationsLogRequest {
 
    private String logbookId;
    private String operationType;
    private LocalDateTime operationDateTime;
    private Long userId;
    private String userRole;
    private String affectedProductId;

    private Map<String, Object> detailData;
}