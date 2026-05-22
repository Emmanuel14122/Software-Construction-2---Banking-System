package app.application.usecases;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import app.domain.Exceptions.BusinessException;
import app.domain.models.Transfer;
import app.domain.models.enums.TransferStatus;
import app.domain.services.transfer.SaveTransfer;
import app.domain.services.transfer.UpdateTransfer;
import app.domain.services.transfer.FindTransfer;
import app.domain.services.transfer.ExistsTransfer;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Service
public class TransferUseCase implements app.domain.ports.in.TransferUseCase {
 
    @Autowired
    private SaveTransfer saveTransfer;
    @Autowired
    private UpdateTransfer updateTransfer;
    @Autowired
    private FindTransfer findTransfer;
    @Autowired
    private ExistsTransfer existsTransfer;
 
    public TransferUseCase(SaveTransfer saveTransfer,
                           UpdateTransfer updateTransfer,
                           FindTransfer findTransfer,
                           ExistsTransfer existsTransfer) {
        this.saveTransfer = saveTransfer;
        this.updateTransfer = updateTransfer;
        this.findTransfer = findTransfer;
        this.existsTransfer = existsTransfer;
    }
 
    @Override
    public void save(Transfer transfer) throws BusinessException {
        saveTransfer.save(transfer);
    }
 
    @Override
    public void update(Transfer transfer) throws BusinessException {
        updateTransfer.update(transfer);
    }
 
    @Override
    public Transfer findById(Long transferId) throws BusinessException {
        return findTransfer.findById(transferId);
    }
 
    @Override
    public List<Transfer> findByAccount(String accountNumber) throws BusinessException {
        return findTransfer.findByAccount(accountNumber);
    }
 
    @Override
    public List<Transfer> findByOriginAccount(String originAccount) throws BusinessException {
        return findTransfer.findByOriginAccount(originAccount);
    }
 
    @Override
    public List<Transfer> findByStatus(TransferStatus status) throws BusinessException {
        return findTransfer.findByStatus(status);
    }
 
    @Override
    public List<Transfer> findByCreatorUser(Long creatorUserId) throws BusinessException {
        return findTransfer.findByCreatorUser(creatorUserId);
    }
 
    @Override
    public List<Transfer> findByCompanyAndStatus(String companyNit, TransferStatus status) throws BusinessException {
        return findTransfer.findByCompanyAndStatus(companyNit, status);
    }
 
    @Override
    public List<Transfer> findWaitingApprovalExpiredBefore(LocalDateTime now) throws BusinessException {
        return findTransfer.findWaitingApprovalExpiredBefore(now);
    }
 
    @Override
    public List<Transfer> findAll() {
        return findTransfer.findAll();
    }
 
    @Override
    public boolean existsById(Long transferId) {
        return existsTransfer.existsById(transferId);
    }
 
}
 