package app.domain.models;

import java.sql.Date;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public abstract class UserSystem {

    private int id_user;
    private String id_related;
    private String full_name;
    private String id_identification;
    private String email;
    private String phone;
    private Date birthDate;
    private String address;
    private String system_role;
    private String user_status;

}
