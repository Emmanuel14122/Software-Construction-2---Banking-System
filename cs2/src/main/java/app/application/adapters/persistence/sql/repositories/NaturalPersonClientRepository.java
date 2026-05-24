package app.application.adapters.persistence.sql.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import app.application.adapters.persistence.sql.entities.NaturalPersonClientEntity;

public interface NaturalPersonClientRepository extends JpaRepository<NaturalPersonClientEntity, Long>{
    
    NaturalPersonClientEntity findByIdentification(String identification);

    boolean existsByIdentification(String identification);

    boolean existsByEmail(String email);

    @Transactional
    void deleteByIdentification(String identification);
}
