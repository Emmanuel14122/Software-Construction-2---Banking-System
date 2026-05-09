package app.application.adapters.api.request;
 
import app.domain.models.enums.RolUser;
import lombok.Getter;
import lombok.Setter;
 
import java.util.Date;
import app.domain.models.enums.UserStatus;
 
@Getter
@Setter
public class UserSystemRequest {
 
    private Long idUser;
    private String idRelated;
    private String fullName;
    private String idIdentification;
    private String email;
    private String phone;
    private Date birthDate;
    private String address;
    private RolUser systemRole;
    private UserStatus userStatus;
    private String username;
    private String password;
}
