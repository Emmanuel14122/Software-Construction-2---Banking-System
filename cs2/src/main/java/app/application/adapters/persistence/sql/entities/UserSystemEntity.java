package app.application.adapters.persistence.sql.entities;

import java.time.LocalDate;

import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_system")
@Getter
@Setter

public class UserSystemEntity {

    @Id
    private Long userId;
    private ClientEntity relatedClient;
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
