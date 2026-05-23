package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "general_banking_products")
@Getter
@Setter

public class GeneralBankingProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "category")
    private String category;
    
    @Column(name = "requires_approval")
    private boolean requiresApproval;
    
}
