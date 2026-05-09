package app.application.adapters.api.response;
 
import app.domain.models.enums.TransferStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
public record TransferResponse(
    Long transferId,
    String originAccount,
    String destinationAccount,
    BigDecimal amount,
    LocalDateTime creationDate,
    LocalDateTime approvalDate,
    TransferStatus transferStatus,
    Long creatorUserId,
    Long approverUserId,
    LocalDateTime expirationCheckAt
) {}