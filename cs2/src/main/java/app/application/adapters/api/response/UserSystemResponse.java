package app.application.adapters.api.response;
 
import app.domain.models.enums.RolUser;
import app.domain.models.enums.UserStatus;
import java.util.Date;
 
public record UserSystemResponse(
    Long idUser,
    String idRelated,
    String fullName,
    String idIdentification,
    String email,
    String phone,
    Date birthDate,
    String address,
    RolUser systemRole,
    UserStatus userStatus,
    String username,
    String password
) {}