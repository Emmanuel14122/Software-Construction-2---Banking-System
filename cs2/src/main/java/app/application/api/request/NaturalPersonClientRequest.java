package app.application.api.request;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;

 
@Getter
@Setter
@NoArgsConstructor
public class NaturalPersonClientRequest extends ClientRequest {
 
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String identification;
    @NotNull(message = "Birth date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}

