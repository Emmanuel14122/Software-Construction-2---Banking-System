package app.domain.ports;

import java.util.List;
import java.util.Optional;

import app.domain.models.UserSystem;
import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;

public interface UserSystemPort {

    Optional<UserSystem> findById(Long userId);

    Optional<UserSystem> findByDocument(String identification);

    Optional<UserSystem> findByUsername(String username);

    Optional<UserSystem> findByEmail(String email);

    List<UserSystem> findAll();

    List<UserSystem> findByRole(UserRole role);

    List<UserSystem> findByStatus(UserStatus status);

    List<UserSystem> findByRoleAndStatus(UserRole role, UserStatus status);

 
    boolean existsByDocument(String identification);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void save(UserSystem user);

    void update(UserSystem user);

    void deleteByDocument(String identification);
}
