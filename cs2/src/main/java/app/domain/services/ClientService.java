package app.domain.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.NotFoundException;
import app.domain.models.CompanyClient;
import app.domain.models.NaturalPersonClient;
import app.domain.models.enums.ClientStatus;
import app.domain.ports.ClientPort;

@Service
public class ClientService {

    private final ClientPort clientPort;

    public ClientService(ClientPort clientPort) {
        this.clientPort = clientPort;
    }

    /**
     * Registra una nueva persona natural en el sistema.
     */
    public void registerNaturalPerson(NaturalPersonClient client) {
        if (clientPort.existsNaturalPersonByDocument(client.getIdentification())) {
            throw new BusinessException(
                "A natural person with identification " + client.getIdentification() + " already exists.");
        }
        if (clientPort.existsByEmail(client.getEmail())) {
            throw new BusinessException(
                "The email " + client.getEmail() + " is already registered in the system.");
        }
        validateAdultAge(client.getBirthDate());

        client.setClientStatus(ClientStatus.Active);
        clientPort.saveNaturalPerson(client);
    }

    /**
     * Actualiza los datos de una persona natural existente.
     */
    public void updateNaturalPerson(NaturalPersonClient client) {
        clientPort.findNaturalPersonByDocument(client.getIdentification())
            .orElseThrow(() -> new NotFoundException(
                "Natural person with identification " + client.getIdentification() + " not found."));

        clientPort.updateNaturalPerson(client);
    }

    /**
     * Obtiene una persona natural por su identificación.
     */
    public NaturalPersonClient getNaturalPersonByDocument(String identification) {
        return clientPort.findNaturalPersonByDocument(identification)
            .orElseThrow(() -> new NotFoundException(
                "Natural person with identification " + identification + " not found."));
    }

    /**
     * Retorna todas las personas naturales registradas.
     * Acceso típico del Analista Interno.
     */
    public List<NaturalPersonClient> getAllNaturalPersons() {
        return clientPort.findAllNaturalPersons();
    }

    /**
     * Registra una nueva empresa cliente en el sistema.
     */
    public void registerCompany(CompanyClient company) {
        if (clientPort.existsCompanyByNit(company.getNit())) {
            throw new BusinessException(
                "A company with NIT " + company.getNit() + " already exists.");
        }
        if (clientPort.existsByEmail(company.getEmail())) {
            throw new BusinessException(
                "The email " + company.getEmail() + " is already registered in the system.");
        }

        // Validar que el representante legal exista como persona natural
        NaturalPersonClient legalRepresentative = company.getLegalRepresentative();

        if (legalRepresentative == null) {
            throw new NotFoundException("Legal representative is required.");
        }

        if (legalRepresentative.getClientStatus() != ClientStatus.Active) {
        throw new BusinessException("The legal representative must be an active client.");
        }

        company.setClientStatus(ClientStatus.Active);
        clientPort.saveCompany(company);
    }

    /**
     * Actualiza los datos de una empresa existente.
     */
    public void updateCompany(CompanyClient company) {
        clientPort.findCompanyByNit(company.getNit())
            .orElseThrow(() -> new NotFoundException(
                "Company with NIT " + company.getNit() + " not found."));

        clientPort.updateCompany(company);
    }

    /**
     * Obtiene una empresa por su NIT.
     *
     */
    public CompanyClient getCompanyByNit(String nit) {
        return clientPort.findCompanyByNit(nit)
            .orElseThrow(() -> new NotFoundException(
                "Company with NIT " + nit + " not found."));
    }

    /**
     * Retorna todas las empresas registradas.
     */
    public List<CompanyClient> getAllCompanies() {
        return clientPort.findAllCompanies();
    }

    
    // Validaciones de dominio (reutilizables por otros servicios)

    public void validateClientIsActive(String identification) {
        // Intentar como persona natural primero
        var naturalOpt = clientPort.findNaturalPersonByDocument(identification);
        if (naturalOpt.isPresent()) {
            ClientStatus status = naturalOpt.get().getClientStatus();
            if (status == ClientStatus.Inactive || status == ClientStatus.Blocked) {
                throw new BusinessException(
                    "Cannot operate: the client with identification " + identification
                        + " is " + status + ".");
            }
            return;
        }

        // Intentar como empresa
        var companyOpt = clientPort.findCompanyByNit(identification);
        if (companyOpt.isPresent()) {
            ClientStatus status = companyOpt.get().getClientStatus();
            if (status == ClientStatus.Inactive || status == ClientStatus.Blocked) {
                throw new BusinessException(
                    "Cannot operate: the company with NIT " + identification
                        + " is " + status + ".");
            }
            return;
        }

        throw new NotFoundException(
            "Client with identification " + identification + " not found.");
    }

    // Métodos privados de validación
    /**
     * Valida que la fecha de nacimiento corresponda a una persona mayor de edad (>= 18 años).
     */
    private void validateAdultAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new BusinessException("Birth date is required.");
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new BusinessException(
                "The client must be at least 18 years old. Calculated age: " + age + " years.");
        }
    }
}
