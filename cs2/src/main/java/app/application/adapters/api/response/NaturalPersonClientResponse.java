package app.application.adapters.api.response;
 
import java.time.LocalDate;
 

public record NaturalPersonClientResponse(
        String fullName,
        String identification,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        String email
) {}