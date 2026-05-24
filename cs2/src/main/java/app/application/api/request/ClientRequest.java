package app.application.api.request;
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.models.enums.ClientStatus;
 
@Getter
@Setter
@NoArgsConstructor
public class ClientRequest {
    
    private Long id;
    @NotBlank(message= "Address is required")
    private String address;
    @NotBlank(message= "Phone number is required")
    @Size(min = 7, max= 15, message= "Phone number must be between 7 and 15 digits")
    private String phoneNumber;
    @NotBlank(message= "Email is required")
    @Email(message = "Email must contain @ and a valid domain")
    private String email;
    private ClientStatus clientStatus;
}
