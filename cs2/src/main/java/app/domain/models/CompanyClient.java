package app.domain.models;

import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CompanyClient extends Client {

    @NotBlank(message ="Company name is required")
    private String companyName;
    @Id
    private String nit;
    @NotBlank(message ="Legal resentative is required")
    private String legalRepresentative;
}