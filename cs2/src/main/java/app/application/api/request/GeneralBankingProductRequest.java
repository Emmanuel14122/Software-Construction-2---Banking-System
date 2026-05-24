package app.application.api.request;
 
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class GeneralBankingProductRequest {
 
    private String productCode;
    private String productName;
    private String category;
    private boolean requiresApproval;
}
