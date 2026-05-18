package app.application.adapters.api.request;
 
import java.time.LocalDate;

import app.domain.models.enums.UserRole;
import lombok.Getter;
import lombok.Setter;


import app.domain.models.enums.UserStatus;
 
@Getter
@Setter
public class UserSystemRequest {
 
    private Long userId;
    private ClientRequest relatedClient;
    private String fullName;
    private String identification;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private UserRole systemRole;
    private UserStatus userStatus;
    private String username;
    private String password;
}
