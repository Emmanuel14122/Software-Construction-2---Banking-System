package app.domain.models;


import java.time.LocalDate;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class NaturalPersonClient extends Client{

    private String identification;
    private String fullName;
    private LocalDate birthDate;
}