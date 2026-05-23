package app.application.adapters.persistence.sql.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_holder_id")
    private String accountHolderId;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @Column(name = "currency")
    private String currency;

    @Column(name = "account_status")
    private String accountStatus;

    @Column(name = "opening_date")
    private LocalDate openingDate;
    
}
