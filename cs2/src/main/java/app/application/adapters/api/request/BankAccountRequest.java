package app.application.adapters.api.request;
 
import app.domain.models.enums.AccountType;
import app.domain.models.enums.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import app.domain.models.enums.AccountStatus;
 
@Getter
@Setter
public class BankAccountRequest {
    
    private String accountNumber;
    private AccountType accountType;
    private String accountHolderId;
    private BigDecimal currentBalance;
    private Currency currency;
    private AccountStatus accountStatus;
    private LocalDate openingDate;
}