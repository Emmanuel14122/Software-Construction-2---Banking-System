package app.domain.models;

import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class GeneralBankingProduct {
    private String product_code;
    private String product_name;
    private String category;
    private boolean requires_approval;
}
