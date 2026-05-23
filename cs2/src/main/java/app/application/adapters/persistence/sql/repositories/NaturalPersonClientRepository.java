package app.application.adapters.persistence.sql.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import app.application.adapters.persistence.sql.entities.NaturalPersonClientEntity;

public interface NaturalPersonClientRepository extends JpaRepository<NaturalPersonClientEntity, Long>{
    
}
