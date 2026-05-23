package app.application.usecases;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import app.domain.Exceptions.BusinessException;
import app.domain.models.OperationsLog;
import app.domain.services.log.SaveOperationsLog;
import app.domain.services.log.FindOperationsLog;
import app.domain.services.log.ExistsOperationsLog;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Service
public class OperationsLogUseCase implements app.domain.ports.OperationsLogPort {
 
    @Autowired
    private SaveOperationsLog saveOperationsLog;
    @Autowired
    private FindOperationsLog findOperationsLog;
    @Autowired
    private ExistsOperationsLog existsOperationsLog;
 
    public OperationsLogUseCase(SaveOperationsLog saveOperationsLog,
                                 FindOperationsLog findOperationsLog,
                                 ExistsOperationsLog existsOperationsLog) {
        this.saveOperationsLog = saveOperationsLog;
        this.findOperationsLog = findOperationsLog;
        this.existsOperationsLog = existsOperationsLog;
    }
 
    @Override
    public void save(OperationsLog operationsLog) throws BusinessException {
        saveOperationsLog.save(operationsLog);
    }
 
    @Override
    public OperationsLog findById(String logbookId) throws BusinessException {
        return findOperationsLog.findById(logbookId);
    }
 
    @Override
    public List<OperationsLog> findByAffectedProduct(String affectedProductId) throws BusinessException {
        return findOperationsLog.findByAffectedProduct(affectedProductId);
    }
 
    @Override
    public List<OperationsLog> findByUser(Long userId) throws BusinessException {
        return findOperationsLog.findByUser(userId);
    }
 
    @Override
    public List<OperationsLog> findByOperationType(String operationType) throws BusinessException {
        return findOperationsLog.findByOperationType(operationType);
    }
 
    @Override
    public List<OperationsLog> findByDateRange(LocalDateTime from, LocalDateTime to) throws BusinessException {
        return findOperationsLog.findByDateRange(from, to);
    }
 
    @Override
    public List<OperationsLog> findAll() {
        return findOperationsLog.findAll();
    }
 
    @Override
    public boolean existsById(String logbookId) {
        return existsOperationsLog.existsById(logbookId);
    }
 
}
 