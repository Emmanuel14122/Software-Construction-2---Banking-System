package app.domain.ports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import app.domain.models.BankAccount;
import app.domain.models.enums.AccountStatus;

public interface BankAccountPort {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findByTitular(String accountHolderId);

    List<BankAccount> findByTitularAndStatus(String accountHolderId, AccountStatus status);

    List<BankAccount> findAll();

    boolean existsByAccountNumber(String accountNumber);

    void save(BankAccount bankAccount);

    void update(BankAccount bankAccount);

    void updateBalance(String accountNumber, BigDecimal newBalance);
}
