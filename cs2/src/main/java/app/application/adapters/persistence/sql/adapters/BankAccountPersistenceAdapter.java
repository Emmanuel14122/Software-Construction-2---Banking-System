package app.application.adapters.persistence.sql.adapters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.repositories.BankAccountRepository;
import app.domain.models.BankAccount;
import app.domain.models.enums.AccountStatus;
import app.domain.models.enums.AccountType;
import app.domain.models.enums.Currency;
import app.domain.ports.BankAccountPort;

@Service
public class BankAccountPersistenceAdapter implements BankAccountPort {
    
    private final BankAccountRepository bankAccountRepository;

    public BankAccountPersistenceAdapter(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return bankAccountRepository.findById(accountNumber).map(this::toModel);
    }

    @Override
    public List<BankAccount> findByTitular(String accountHolderId) {
        return bankAccountRepository.findByAccountHolderId(accountHolderId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankAccount> findByTitularAndStatus(String accountHolderId, AccountStatus status) {
        return bankAccountRepository
                .findByAccountHolderIdAndAccountStatus(accountHolderId, status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return bankAccountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(toEntity(bankAccount));
    }

    @Override
    public void update(BankAccount bankAccount) {
        bankAccountRepository.findById(bankAccount.getAccountNumber()).ifPresent(existing -> {
            existing.setAccountType(bankAccount.getAccountType() != null
                    ? bankAccount.getAccountType().name() : null);
            existing.setAccountHolderId(bankAccount.getAccountHolderId());
            existing.setCurrentBalance(bankAccount.getCurrentBalance());
            existing.setCurrency(bankAccount.getCurrency() != null
                    ? bankAccount.getCurrency().name() : null);
            existing.setAccountStatus(bankAccount.getAccountStatus() != null
                    ? bankAccount.getAccountStatus().name() : null);
            existing.setOpeningDate(bankAccount.getOpeningDate());
            bankAccountRepository.save(existing);
        });
    }

    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) {
        bankAccountRepository.findById(accountNumber).ifPresent(existing -> {
            existing.setCurrentBalance(newBalance);
            bankAccountRepository.save(existing);
        });
    }


    private BankAccountEntity toEntity(BankAccount bankAccount) {
        BankAccountEntity entity = new BankAccountEntity();
        entity.setAccountNumber(bankAccount.getAccountNumber());
        entity.setAccountType(bankAccount.getAccountType() != null
                ? bankAccount.getAccountType().name() : null);
        entity.setAccountHolderId(bankAccount.getAccountHolderId());
        entity.setCurrentBalance(bankAccount.getCurrentBalance());
        entity.setCurrency(bankAccount.getCurrency() != null
                ? bankAccount.getCurrency().name() : null);
        entity.setAccountStatus(bankAccount.getAccountStatus() != null
                ? bankAccount.getAccountStatus().name() : null);
        entity.setOpeningDate(bankAccount.getOpeningDate());
        return entity;
    }

    private BankAccount toModel(BankAccountEntity entity) {
        if (entity == null) return null;
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(entity.getAccountNumber());
        bankAccount.setAccountType(entity.getAccountType() != null
                ? AccountType.valueOf(entity.getAccountType()) : null);
        bankAccount.setAccountHolderId(entity.getAccountHolderId());
        bankAccount.setCurrentBalance(entity.getCurrentBalance());
        bankAccount.setCurrency(entity.getCurrency() != null
                ? Currency.valueOf(entity.getCurrency()) : null);
        bankAccount.setAccountStatus(entity.getAccountStatus() != null
                ? AccountStatus.valueOf(entity.getAccountStatus()) : null);
        bankAccount.setOpeningDate(entity.getOpeningDate());
        return bankAccount;
    }
}
