package app.domain.models;

import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class BankAccount {
    
    private String accountNumber;
    private String accountType;
    private String id_titular;
    private double currentBalance;
    private String currency;
    private String accountStatus;
    private String openingDate;

}
