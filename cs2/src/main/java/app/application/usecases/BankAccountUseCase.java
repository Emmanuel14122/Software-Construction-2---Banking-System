package app.application.usecases;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.enums.AccountStatus;
import app.domain.services.account.SaveBankAccount;
import app.domain.services.account.UpdateBankAccount;
import app.domain.services.account.UpdateBankAccountBalance;
import app.domain.services.account.FindBankAccount;
import app.domain.services.account.ExistsBankAccount;
 
import java.math.BigDecimal;
import java.util.List;
 
@Service
public class BankAccountUseCase implements app.domain.ports.in.BankAccountUseCase {
 
    @Autowired
    private SaveBankAccount saveBankAccount;
    @Autowired
    private UpdateBankAccount updateBankAccount;
    @Autowired
    private UpdateBankAccountBalance updateBankAccountBalance;
    @Autowired
    private FindBankAccount findBankAccount;
    @Autowired
    private ExistsBankAccount existsBankAccount;
 
    public BankAccountUseCase(SaveBankAccount saveBankAccount,
                               UpdateBankAccount updateBankAccount,
                               UpdateBankAccountBalance updateBankAccountBalance,
                               FindBankAccount findBankAccount,
                               ExistsBankAccount existsBankAccount) {
        this.saveBankAccount = saveBankAccount;
        this.updateBankAccount = updateBankAccount;
        this.updateBankAccountBalance = updateBankAccountBalance;
        this.findBankAccount = findBankAccount;
        this.existsBankAccount = existsBankAccount;
    }
 
    @Override
    public void save(BankAccount bankAccount) throws BusinessException {
        saveBankAccount.save(bankAccount);
    }
 
    @Override
    public void update(BankAccount bankAccount) throws BusinessException {
        updateBankAccount.update(bankAccount);
    }
 
    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) throws BusinessException {
        updateBankAccountBalance.updateBalance(accountNumber, newBalance);
    }
 
    @Override
    public BankAccount findByAccountNumber(String accountNumber) throws BusinessException {
        return findBankAccount.findByAccountNumber(accountNumber);
    }
 
    @Override
    public List<BankAccount> findByTitular(String accountHolderId) throws BusinessException {
        return findBankAccount.findByTitular(accountHolderId);
    }
 
    @Override
    public List<BankAccount> findByTitularAndStatus(String accountHolderId, AccountStatus status) throws BusinessException {
        return findBankAccount.findByTitularAndStatus(accountHolderId, status);
    }
 
    @Override
    public List<BankAccount> findAll() {
        return findBankAccount.findAll();
    }
 
    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return existsBankAccount.existsByAccountNumber(accountNumber);
    }
 
}
 