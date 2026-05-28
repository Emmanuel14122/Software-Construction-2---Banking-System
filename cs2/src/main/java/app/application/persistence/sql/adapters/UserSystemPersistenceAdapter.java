package app.application.persistence.sql.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.application.persistence.sql.entities.UserSystemEntity;
import app.application.persistence.sql.repositories.UserSystemRepository;
import app.domain.models.Client;
import app.domain.models.UserSystem;
import app.domain.models.enums.ClientType;
import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;
import app.domain.ports.UserSystemPort;

@Service
public class UserSystemPersistenceAdapter implements UserSystemPort {
    private final UserSystemRepository userSystemRepository;

    public UserSystemPersistenceAdapter(UserSystemRepository userSystemRepository) {
        this.userSystemRepository = userSystemRepository;
    }

    @Override
    public Optional<UserSystem> findById(Long userId) {
        return userSystemRepository.findById(userId).map(this::toModel);
    }

    @Override
    public Optional<UserSystem> findByDocument(String identification) {
        return Optional.ofNullable(toModel(userSystemRepository.findByIdentification(identification)));
    }

    @Override
    public Optional<UserSystem> findByUsername(String username) {
        return Optional.ofNullable(toModel(userSystemRepository.findByUsername(username)));
    }

    @Override
    public Optional<UserSystem> findByEmail(String email) {
        return Optional.ofNullable(toModel(userSystemRepository.findByEmail(email)));
    }

    @Override
    public List<UserSystem> findAll() {
        return userSystemRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSystem> findByRole(UserRole role) {
        return userSystemRepository.findBySystemRole(role.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSystem> findByStatus(UserStatus status) {
        return userSystemRepository.findByUserStatus(status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSystem> findByRoleAndStatus(UserRole role, UserStatus status) {
        return userSystemRepository.findBySystemRoleAndUserStatus(role.name(), status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByDocument(String identification) {
        return userSystemRepository.existsByIdentification(identification);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userSystemRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userSystemRepository.existsByEmail(email);
    }

    // ✅ FIX: ahora setea el userId generado por la BD de vuelta al objeto
    @Override
    public void save(UserSystem user) {
        UserSystemEntity saved = userSystemRepository.save(toEntity(user));
        user.setUserId(saved.getUserId());
    }

    @Override
    public void update(UserSystem user) {
        UserSystemEntity existing = userSystemRepository.findByIdentification(user.getIdentification());
        if (existing != null) {
            existing.setFullName(user.getFullName());
            existing.setPhone(user.getPhone());
            existing.setEmail(user.getEmail());
            existing.setAddress(user.getAddress());
            existing.setBirthDate(user.getBirthDate());
            existing.setUsername(user.getUsername());
            existing.setPassword(user.getPassword());
            existing.setSystemRole(user.getSystemRole() != null ? user.getSystemRole().name() : null);
            existing.setUserStatus(user.getUserStatus() != null ? user.getUserStatus().name() : null);
            userSystemRepository.save(existing);
        }
    }

    @Override
    @Transactional
    public void deleteByDocument(String identification) {
        userSystemRepository.deleteByIdentification(identification);
    }

    private UserSystemEntity toEntity(UserSystem user) {
        UserSystemEntity entity = new UserSystemEntity();
        entity.setFullName(user.getFullName());
        entity.setIdentification(user.getIdentification());
        entity.setEmail(user.getEmail());
        entity.setPhone(user.getPhone());
        entity.setBirthDate(user.getBirthDate());
        entity.setAddress(user.getAddress());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setSystemRole(user.getSystemRole() != null ? user.getSystemRole().name() : null);
        entity.setUserStatus(user.getUserStatus() != null ? user.getUserStatus().name() : null);
        entity.setClientType(user.getClientType() != null ? user.getClientType().name() : null);
        entity.setRelatedClientId(user.getRelatedClientId() != null
                ? user.getRelatedClientId().getId().toString()
                : null);
        return entity;
    }

    private UserSystem toModel(UserSystemEntity entity) {
        if (entity == null) return null;
        UserSystem user = new UserSystem();
        user.setUserId(entity.getUserId());
        user.setFullName(entity.getFullName());
        user.setIdentification(entity.getIdentification());
        user.setEmail(entity.getEmail());
        user.setPhone(entity.getPhone());
        user.setBirthDate(entity.getBirthDate());
        user.setAddress(entity.getAddress());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setSystemRole(entity.getSystemRole() != null
                ? UserRole.valueOf(entity.getSystemRole()) : null);
        user.setUserStatus(entity.getUserStatus() != null
                ? UserStatus.valueOf(entity.getUserStatus()) : null);
        user.setClientType(entity.getClientType() != null
                ? ClientType.valueOf(entity.getClientType()) : null);
        if (entity.getRelatedClientId() != null) {
            Client client = new Client() {};
            client.setId(Long.valueOf(entity.getRelatedClientId()));
            user.setRelatedClientId(client);
        }
        return user;
    }
}