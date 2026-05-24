package app.application.api.request;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
public class CompanyClientRequest extends ClientRequest {
 
    @NotBlank(message ="Company name is required")
    private String companyName;
    private String nit;
    @NotBlank(message ="Legal resentative is required")
    private String legalRepresentativeId;
}
 
