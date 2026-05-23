package app.application.adapters.persistence.sql.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import app.application.adapters.persistence.sql.entities.CompanyClientEntity;

public interface CompanyClientRepository extends JpaRepository<CompanyClientEntity, Long> {
    
}
