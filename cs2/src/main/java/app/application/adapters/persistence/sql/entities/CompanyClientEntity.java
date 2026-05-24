package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_clients")
@Getter
@Setter
public class CompanyClientEntity extends ClientEntity {

    @Column(name = "nit", unique = true, nullable = false)
    private String nit;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @ManyToOne
    @JoinColumn(name = "legal_representative", nullable = false)
    private NaturalPersonClientEntity legalRepresentative;

}
