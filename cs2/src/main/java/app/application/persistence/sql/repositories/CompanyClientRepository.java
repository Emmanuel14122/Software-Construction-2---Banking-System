package app.application.persistence.sql.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import app.application.persistence.sql.entities.CompanyClientEntity;

public interface CompanyClientRepository extends JpaRepository<CompanyClientEntity, Long> {
    
    CompanyClientEntity findByNit(String nit);

    boolean existsByNit(String nit);

    boolean existsByEmail(String email);

    @Transactional
    void deleteByNit(String nit);
}
