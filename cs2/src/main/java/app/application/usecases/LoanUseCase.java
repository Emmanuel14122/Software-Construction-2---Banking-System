package app.application.usecases;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import app.domain.Exceptions.BusinessException;
import app.domain.models.Loan;
import app.domain.models.enums.LoanStatus;
import app.domain.services.loan.SaveLoan;
import app.domain.services.loan.UpdateLoan;
import app.domain.services.loan.FindLoan;
import app.domain.services.loan.ExistsLoan;
 
import java.util.List;
 
@Service
public class LoanUseCase implements app.domain.ports.in.LoanUseCase {
 
    @Autowired
    private SaveLoan saveLoan;
    @Autowired
    private UpdateLoan updateLoan;
    @Autowired
    private FindLoan findLoan;
    @Autowired
    private ExistsLoan existsLoan;
 
    public LoanUseCase(SaveLoan saveLoan,
                       UpdateLoan updateLoan,
                       FindLoan findLoan,
                       ExistsLoan existsLoan) {
        this.saveLoan = saveLoan;
        this.updateLoan = updateLoan;
        this.findLoan = findLoan;
        this.existsLoan = existsLoan;
    }
 
    @Override
    public void save(Loan loan) throws BusinessException {
        saveLoan.save(loan);
    }
 
    @Override
    public void update(Loan loan) throws BusinessException {
        updateLoan.update(loan);
    }
 
    @Override
    public Loan findById(Long loanId) throws BusinessException {
        return findLoan.findById(loanId);
    }
 
    @Override
    public List<Loan> findByClient(String clientId) throws BusinessException {
        return findLoan.findByClient(clientId);
    }
 
    @Override
    public List<Loan> findByClientAndStatus(String clientId, LoanStatus status) throws BusinessException {
        return findLoan.findByClientAndStatus(clientId, status);
    }
 
    @Override
    public List<Loan> findByStatus(LoanStatus status) throws BusinessException {
        return findLoan.findByStatus(status);
    }
 
    @Override
    public List<Loan> findAll() {
        return findLoan.findAll();
    }
 
    @Override
    public boolean existsById(Long loanId) {
        return existsLoan.existsById(loanId);
    }
 
}