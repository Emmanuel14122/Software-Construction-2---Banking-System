package app.application.adapters.persistence.sql.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.application.adapters.persistence.sql.entities.TransferEntity;

public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
    
    List<TransferEntity> findByOriginAccountOrDestinationAccount(String originAccount, String destinationAccount);

    List<TransferEntity> findByOriginAccount(String originAccount);

    List<TransferEntity> findByTransferStatus(String transferStatus);

    List<TransferEntity> findByCreatorUserId(Long creatorUserId);

    /**
     * Busca transferencias cuya cuenta origen pertenezca a una empresa (identificada
     * por la lista de sus números de cuenta) y estén en un estado específico.
     */
    @Query("SELECT t FROM TransferEntity t WHERE t.originAccount IN :accountNumbers AND t.transferStatus = :status")
    List<TransferEntity> findByOriginAccountInAndTransferStatus(
            @Param("accountNumbers") List<String> accountNumbers,
            @Param("status") String status);

    List<TransferEntity> findByTransferStatusAndExpirationCheckAtBefore(String transferStatus,
                                                                          LocalDateTime now);
}
