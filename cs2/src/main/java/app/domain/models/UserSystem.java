package app.domain.models;

import java.time.LocalDate;

import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UserSystem {

    private Long userId;
    private Client relatedClientId;
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
