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
    
    @Column(name = "identification", unique = true, nullable = false)
    private String identification;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
}
