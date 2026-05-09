package app.domain.models;


import java.time.LocalDate;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class NaturalPersonClient extends Client{

    private String fullName;
    @Id
    @Column(nullable = false, unique = true)
    private String idIdentification;
    private LocalDate birthDate;
}