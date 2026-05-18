package app.application.adapters.persistence.sql.entities;

import app.domain.models.NaturalPersonClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_clients")
@Getter
@Setter
public class CompanyClientEntity extends ClientEntity {

    @Column(unique = true, nullable = false)
    private String nit;
    private String companyName;
    private NaturalPersonClient legalRepresentative;

}
