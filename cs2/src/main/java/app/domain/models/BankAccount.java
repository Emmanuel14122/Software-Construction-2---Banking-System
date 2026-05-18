package app.domain.models;
import java.math.BigDecimal;

import app.domain.models.enums.AccountStatus;
import app.domain.models.enums.AccountType;
import app.domain.models.enums.Currency;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor

public class BankAccount {

    private String accountNumber;
    private AccountType accountType;
    private String accountHolderId;
    private BigDecimal currentBalance;
    private Currency currency;
    private AccountStatus accountStatus;
    private LocalDate openingDate;

}
