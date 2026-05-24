package app.application.adapters.persistence.sql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import app.application.adapters.persistence.sql.entities.BankAccountEntity;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, String> {
    
    List<BankAccountEntity> findByAccountHolderId(String accountHolderId);

    List<BankAccountEntity> findByAccountHolderIdAndAccountStatus(String accountHolderId, String accountStatus);

    boolean existsByAccountNumber(String accountNumber);
}
