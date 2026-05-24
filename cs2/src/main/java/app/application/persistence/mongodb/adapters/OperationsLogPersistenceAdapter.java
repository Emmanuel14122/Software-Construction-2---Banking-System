package app.application.persistence.mongodb.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import app.application.persistence.mongodb.documents.OperationsLogDocument;
import app.application.persistence.mongodb.repositories.OperationsLogMongoRepository;
import app.domain.models.OperationsLog;
import app.domain.ports.OperationsLogPort;

public class OperationsLogPersistenceAdapter implements OperationsLogPort{
    
    private final OperationsLogMongoRepository repository;

    public OperationsLogPersistenceAdapter(OperationsLogMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<OperationsLog> findById(String logbookId) {
        return repository.findById(logbookId).map(this::toModel);
    }

    @Override
    public List<OperationsLog> findByAffectedProduct(String affectedProductId) {

        return repository.findByAffectedProductId(affectedProductId).stream().map(this::toModel).toList();
    }

    @Override
    public List<OperationsLog> findByUser(Long userId) {

        return repository.findByUserId(userId).stream().map(this::toModel).toList();
    }

    @Override
    public List<OperationsLog> findByOperationType(String operationType) {

        return repository.findByOperationType(operationType).stream().map(this::toModel).toList();
    }

    @Override
    public List<OperationsLog> findByDateRange(LocalDateTime from,LocalDateTime to) {

        return repository.findByOperationDateTimeBetween(from, to).stream().map(this::toModel).toList();
    }

    @Override
    public List<OperationsLog> findAll() {
        return repository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public boolean existsById(String logbookId) {
        return repository.existsById(logbookId);
    }

    @Override
    public void save(OperationsLog operationsLog) {

        OperationsLogDocument document = toDocument(operationsLog);

        repository.save(document);
    }

    private OperationsLog toModel(OperationsLogDocument document) {

        return OperationsLog.builder()
                .logbookId(document.getLogbookId())
                .operationType(document.getOperationType())
                .operationDateTime(document.getOperationDateTime())
                .userId(document.getUserId())
                .userRole(document.getUserRole())
                .affectedProductId(document.getAffectedProductId())
                .detailData(document.getDetailData())
                .build();
    }

    private OperationsLogDocument toDocument(OperationsLog operationsLog) {

        OperationsLogDocument document = new OperationsLogDocument();

        document.setLogbookId(operationsLog.getLogbookId());
        document.setOperationType(operationsLog.getOperationType());

        document.setOperationDateTime(operationsLog.getOperationDateTime());

        document.setUserId(operationsLog.getUserId());

        document.setUserRole(operationsLog.getUserRole());

        document.setAffectedProductId(operationsLog.getAffectedProductId());

        document.setDetailData(operationsLog.getDetailData());

        return document;
    }
}
