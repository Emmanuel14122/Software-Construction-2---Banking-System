package app.domain.models;

import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_clients")
@Getter
@Setter
@NoArgsConstructor
public class CompanyClient extends Client {

    private String companyName;
    @Id
    private String nit;
    private String legalRepresentativeId;
}