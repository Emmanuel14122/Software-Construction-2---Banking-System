package app.application.adapters.persistence.sql.entities;

import app.domain.models.enums.ClientStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Inheritance(strategy = InheritanceType.JOINED)
@MappedSuperclass
@Getter
@Setter

public abstract class ClientEntity {

    private String address;
    private String phoneNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private ClientStatus clientStatus;
    
}
