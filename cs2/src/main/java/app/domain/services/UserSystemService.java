package app.domain.services;

import java.util.List;

import org.springframework.stereotype.Service;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.NotFoundException;
import app.domain.models.UserSystem;
import app.domain.models.enums.UserRole;
import app.domain.models.enums.UserStatus;
import app.domain.ports.UserSystemPort;

@Service
public class UserSystemService {

    private final UserSystemPort userSystemPort;

    public UserSystemService(UserSystemPort userSystemPort) {
        this.userSystemPort = userSystemPort;
    }

    //Registra un nuevo usuario en el sistema.
    public void registerUser(UserSystem user) {
        if (userSystemPort.existsByDocument(user.getIdentification())) {
            throw new BusinessException(
                "A user with identification " + user.getIdentification() + " already exists.");
        }
        if (userSystemPort.existsByUsername(user.getUsername())) {
            throw new BusinessException(
                "Username '" + user.getUsername() + "' is already taken.");
        }
        if (userSystemPort.existsByEmail(user.getEmail())) {
            throw new BusinessException(
                "Email " + user.getEmail() + " is already registered.");
        }

        user.setUserStatus(UserStatus.Active);
        userSystemPort.save(user);
    }

    //Actualiza los datos de un usuario existente.
    public void updateUser(UserSystem user) {
        userSystemPort.findById(user.getUserId())
            .orElseThrow(() -> new NotFoundException(
                "User with ID " + user.getUserId() + " not found."));

        userSystemPort.update(user);
    }

    //Cambia el estado de un usuario (Active, Inactive, Locked).
    public void changeUserStatus(Long userId, UserStatus newStatus) {
        UserSystem user = userSystemPort.findById(userId)
            .orElseThrow(() -> new NotFoundException(
                "User with ID " + userId + " not found."));

        user.setUserStatus(newStatus);
        userSystemPort.update(user);
    }

    // Consultas

    //Obtiene un usuario por su identificador numérico.
    public UserSystem getUserById(Long userId) {
        return userSystemPort.findById(userId)
            .orElseThrow(() -> new NotFoundException(
                "User with ID " + userId + " not found."));
    }

    //Obtiene un usuario por su número de identificación.
    public UserSystem getUserByDocument(String identification) {
        return userSystemPort.findByDocument(identification)
            .orElseThrow(() -> new NotFoundException(
                "User with identification " + identification + " not found."));
    }

    //Obtiene un usuario por su username. Usado en el proceso de autenticación.
    public UserSystem getUserByUsername(String username) {
        return userSystemPort.findByUsername(username)
            .orElseThrow(() -> new NotFoundException(
                "User with username '" + username + "' not found."));
    }

    //Retorna todos los usuarios del sistema.
    public List<UserSystem> getAllUsers() {
        return userSystemPort.findAll();
    }

    //Retorna todos los usuarios con un rol específico.
    public List<UserSystem> getUsersByRole(UserRole role) {
        return userSystemPort.findByRole(role);
    }

    //Retorna todos los usuarios activos con un rol específico.
    public List<UserSystem> getActiveUsersByRole(UserRole role) {
        return userSystemPort.findByRoleAndStatus(role, UserStatus.Active);
    }


    // (Validaciones reutilizables por otros servicios)

    //Valida que un usuario exista y esté activo.
    public void validateUserIsActive(Long userId) {
        UserSystem user = userSystemPort.findById(userId)
            .orElseThrow(() -> new NotFoundException(
                "User with ID " + userId + " not found."));

        if (user.getUserStatus() != UserStatus.Active) {
            throw new BusinessException(
                "User " + userId + " is not active. Current status: " + user.getUserStatus() + ".");
        }
    }

    //Valida que un usuario tenga el rol requerido para ejecutar una operación.
    public void validateUserHasRole(Long userId, UserRole requiredRole) {
        UserSystem user = getUserById(userId);

        if (user.getSystemRole() != requiredRole) {
            throw new BusinessException(
                "User " + userId + " does not have the required role '"
                    + requiredRole + "'. Current role: " + user.getSystemRole() + ".");
        }
    }

    //Desactiva un usuario por su número de identificación.
    public void deactivateUser(String identification) {
        userSystemPort.findByDocument(identification)
            .orElseThrow(() -> new NotFoundException(
                "User with identification " + identification + " not found."));

        userSystemPort.deleteByDocument(identification);
    }
}
