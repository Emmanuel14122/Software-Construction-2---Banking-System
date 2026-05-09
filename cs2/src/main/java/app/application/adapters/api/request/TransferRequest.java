package app.application.adapters.api.request;
 
import lombok.Getter;
import lombok.Setter;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import app.domain.models.enums.TransferStatus;
 
@Getter
@Setter
public class TransferRequest {
    
    private Long transferId;
    private String originAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private LocalDateTime approvalDate;
    private TransferStatus transferStatus;
    private Long creatorUserId;
    private Long approverUserId;
    private LocalDateTime expirationCheckAt;
}