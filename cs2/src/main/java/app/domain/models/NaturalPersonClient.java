package app.domain.models;

import  java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class NaturalPersonClient extends Client{

    @NotBlank(message = "Full name is required")
    private String fullName;
    @Id
    private long id;
    @NotBlank(message = "Birth date is required")
    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private Date birthDate;
}