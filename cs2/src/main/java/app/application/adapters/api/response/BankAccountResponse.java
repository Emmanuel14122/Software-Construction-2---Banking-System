package app.application.adapters.api.response;
 
import app.domain.models.enums.AccountStatus;
import app.domain.models.enums.AccountType;
import app.domain.models.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDate;
 

public record BankAccountResponse(
    String accountNumber,
    AccountType accountType,
    String accountHolderId,
    BigDecimal currentBalance,
    Currency currency,
    AccountStatus accountStatus,
    LocalDate openingDate
) {}