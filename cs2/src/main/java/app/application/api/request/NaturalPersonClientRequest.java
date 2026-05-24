package app.application.api.request;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

 
@Getter
@Setter
@NoArgsConstructor
public class NaturalPersonClientRequest extends ClientRequest {
 
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String identification;
    @NotBlank(message = "Birth date is required")
    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private LocalDate birthDate;
}

