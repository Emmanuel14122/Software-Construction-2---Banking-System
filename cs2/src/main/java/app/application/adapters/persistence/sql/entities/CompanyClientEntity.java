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

    @Column(name = "nit", unique = true)
    private String nit;

    @Column(name = "company_name")
    private String companyName;

    @ManyToOne
    @JoinColumn(name = "legal_representative")
    private NaturalPersonClientEntity legalRepresentative;

}
