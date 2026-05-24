package app.application.adapters.persistence.sql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import app.application.adapters.persistence.sql.entities.UserSystemEntity;

public interface UserSystemRepository extends JpaRepository<UserSystemEntity, Long>{
    
    UserSystemEntity findByIdentification(String identification);

    UserSystemEntity findByUsername(String username);

    UserSystemEntity findByEmail(String email);

    List<UserSystemEntity> findBySystemRole(String systemRole);

    List<UserSystemEntity> findByUserStatus(String userStatus);

    List<UserSystemEntity> findBySystemRoleAndUserStatus(String systemRole, String userStatus);

    boolean existsByIdentification(String identification);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteByIdentification(String identification);
}
