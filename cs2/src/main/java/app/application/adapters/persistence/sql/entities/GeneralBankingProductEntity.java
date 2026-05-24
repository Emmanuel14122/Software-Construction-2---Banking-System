package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "category", nullable = false)
    private String category;
    
    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval;
    
}
