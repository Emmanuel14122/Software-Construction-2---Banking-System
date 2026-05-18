package app.application.adapters.persistence.sql.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "natural_person_clients")
@Getter
@Setter

public class NaturalPersonClientEntity extends ClientEntity {
    
    @Column(unique = true, nullable = false)
    private String identification;
    private String fullName;
    private LocalDate birthDate;
    
}
