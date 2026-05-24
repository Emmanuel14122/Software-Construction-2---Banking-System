package app.application.persistence.mongodb.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import app.application.persistence.mongodb.documents.OperationsLogDocument;

public interface OperationsLogMongoRepository extends MongoRepository<OperationsLogDocument, String> {
    List<OperationsLogDocument> findByAffectedProductId(String affectedProductId);

    List<OperationsLogDocument> findByUserId(Long userId);

    List<OperationsLogDocument> findByOperationType(String operationType);

    List<OperationsLogDocument> findByOperationDateTimeBetween(LocalDateTime from,LocalDateTime to);
}
