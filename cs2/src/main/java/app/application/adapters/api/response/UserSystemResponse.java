package app.application.adapters.api.response;
 
import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;

import java.time.LocalDate;
 
public record UserSystemResponse(
    Long userId,
    ClientResponse relatedClientId,
    String fullName,
    String identification,
    String email,
    String phone,
    LocalDate birthDate,
    String address,
    UserRole systemRole,
    UserStatus userStatus,
    String username,
    String password
) {}