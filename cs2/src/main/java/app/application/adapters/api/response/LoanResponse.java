package app.application.adapters.api.response;
 
import app.domain.models.enums.LoanStatus;
import app.domain.models.enums.LoanType;
import java.math.BigDecimal;
import java.time.LocalDate;
 
public record LoanResponse(
    Long loanId,
    LoanType loanType,
    String clientRequestorId,
    BigDecimal requestedAmount,
    BigDecimal approvedAmount,
    BigDecimal interestRate,
    int termMonths,
    LoanStatus loanStatus,
    LocalDate approvalDate,
    LocalDate disbursementDate,
    String destinationAccountDisbursement,
    Long approverAnalystId

) {}