package app.application.adapters.persistence.sql.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "client_type")
    private String clientType;

    @Column(name = "related_client_id")
    private String relatedClientId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "identification", unique = true, nullable = false)
    private String identification;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "system_role")
    private String systemRole;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
    
}
