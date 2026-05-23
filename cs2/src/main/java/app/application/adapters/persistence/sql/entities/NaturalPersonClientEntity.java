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
@Table(name = "natural_person_clients")
@Getter
@Setter

public class NaturalPersonClientEntity extends ClientEntity {
    
    @Column(name = "identification", unique = true)
    private String identification;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;
    
}
