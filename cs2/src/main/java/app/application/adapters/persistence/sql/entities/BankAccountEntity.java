package app.application.adapters.persistence.sql.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import app.domain.models.enums.AccountStatus;
import app.domain.models.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
public class BankAccountEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String accountNumber;
    private AccountType accountType;
    private String accountHolderId;
    private BigDecimal currentBalance;
    private Currency currency;
    private AccountStatus accountStatus;
    private LocalDate openingDate;
    
}
