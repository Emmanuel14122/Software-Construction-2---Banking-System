package app.application.adapters.api.request;
 
import app.domain.models.enums.LoanType;
import lombok.Getter;
import lombok.Setter;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import app.domain.models.enums.LoanStatus;
 
@Getter
@Setter
public class LoanRequest {
 
    private Long loanId;
    private LoanType loanType;
    private String clientRequestorId;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private int termMonths;
    private LoanStatus loanStatus;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private String destinationAccountDisbursement;
    private Long approverAnalystId;
}