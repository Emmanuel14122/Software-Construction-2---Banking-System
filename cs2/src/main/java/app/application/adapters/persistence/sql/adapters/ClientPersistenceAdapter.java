package app.application.adapters.persistence.sql.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.application.adapters.persistence.sql.entities.CompanyClientEntity;
import app.application.adapters.persistence.sql.entities.NaturalPersonClientEntity;
import app.application.adapters.persistence.sql.repositories.CompanyClientRepository;
import app.application.adapters.persistence.sql.repositories.NaturalPersonClientRepository;
import app.domain.models.CompanyClient;
import app.domain.models.NaturalPersonClient;
import app.domain.models.enums.ClientStatus;
import app.domain.ports.ClientPort;

@Service
public class ClientPersistenceAdapter implements ClientPort{
    
    private final NaturalPersonClientRepository naturalPersonRepository;
    private final CompanyClientRepository companyRepository;

    public ClientPersistenceAdapter(NaturalPersonClientRepository naturalPersonRepository,
                                     CompanyClientRepository companyRepository) {
        this.naturalPersonRepository = naturalPersonRepository;
        this.companyRepository = companyRepository;
    }

    // -------------------------------------------------------------------------
    // Persona Natural – Consultas
    // -------------------------------------------------------------------------

    @Override
    public Optional<NaturalPersonClient> findNaturalPersonByDocument(String identification) {
        return Optional.ofNullable(toNaturalPersonModel(
                naturalPersonRepository.findByIdentification(identification)));
    }

    @Override
    public List<NaturalPersonClient> findAllNaturalPersons() {
        return naturalPersonRepository.findAll().stream()
                .map(this::toNaturalPersonModel)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Empresa – Consultas
    // -------------------------------------------------------------------------

    @Override
    public Optional<CompanyClient> findCompanyByNit(String nit) {
        return Optional.ofNullable(toCompanyModel(companyRepository.findByNit(nit)));
    }

    @Override
    public List<CompanyClient> findAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toCompanyModel)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Verificaciones de existencia
    // -------------------------------------------------------------------------

    @Override
    public boolean existsNaturalPersonByDocument(String identification) {
        return naturalPersonRepository.existsByIdentification(identification);
    }

    @Override
    public boolean existsCompanyByNit(String nit) {
        return companyRepository.existsByNit(nit);
    }

    /**
     * Verifica si el email está en uso en cualquier tipo de cliente.
     * El PDF exige unicidad de email en todo el sistema (personas y empresas).
     */
    @Override
    public boolean existsByEmail(String email) {
        return naturalPersonRepository.existsByEmail(email)
                || companyRepository.existsByEmail(email);
    }

    // -------------------------------------------------------------------------
    // Persona Natural – Persistencia
    // -------------------------------------------------------------------------

    @Override
    public void saveNaturalPerson(NaturalPersonClient client) {
        naturalPersonRepository.save(toNaturalPersonEntity(client));
    }

    @Override
    public void updateNaturalPerson(NaturalPersonClient client) {
        NaturalPersonClientEntity existing =
                naturalPersonRepository.findByIdentification(client.getIdentification());
        if (existing != null) {
            existing.setFullName(client.getFullName());
            existing.setEmail(client.getEmail());
            existing.setPhoneNumber(client.getPhoneNumber());
            existing.setAddress(client.getAddress());
            existing.setBirthDate(client.getBirthDate());
            existing.setClientStatus(client.getClientStatus() != null
                    ? client.getClientStatus().name() : null);
            naturalPersonRepository.save(existing);
        }
    }

    @Override
    @Transactional
    public void deleteNaturalPersonByDocument(String identification) {
        naturalPersonRepository.deleteByIdentification(identification);
    }

    // -------------------------------------------------------------------------
    // Empresa – Persistencia
    // -------------------------------------------------------------------------

    @Override
    public void saveCompany(CompanyClient company) {
        companyRepository.save(toCompanyEntity(company));
    }

    @Override
    public void updateCompany(CompanyClient company) {
        CompanyClientEntity existing = companyRepository.findByNit(company.getNit());
        if (existing != null) {
            existing.setCompanyName(company.getCompanyName());
            existing.setEmail(company.getEmail());
            existing.setPhoneNumber(company.getPhoneNumber());
            existing.setAddress(company.getAddress());
            existing.setClientStatus(company.getClientStatus() != null
                    ? company.getClientStatus().name() : null);
            // El representante legal no se actualiza aquí (requiere flujo de negocio propio)
            companyRepository.save(existing);
        }
    }

    @Override
    @Transactional
    public void deleteCompanyByNit(String nit) {
        companyRepository.deleteByNit(nit);
    }

    // -------------------------------------------------------------------------
    // Mapeo entity ↔ modelo – Persona Natural
    // -------------------------------------------------------------------------

    private NaturalPersonClientEntity toNaturalPersonEntity(NaturalPersonClient client) {
        NaturalPersonClientEntity entity = new NaturalPersonClientEntity();
        entity.setIdentification(client.getIdentification());
        entity.setFullName(client.getFullName());
        entity.setBirthDate(client.getBirthDate());
        entity.setEmail(client.getEmail());
        entity.setPhoneNumber(client.getPhoneNumber());
        entity.setAddress(client.getAddress());
        entity.setClientStatus(client.getClientStatus() != null
                ? client.getClientStatus().name() : null);
        return entity;
    }

    private NaturalPersonClient toNaturalPersonModel(NaturalPersonClientEntity entity) {
        if (entity == null) return null;
        NaturalPersonClient client = new NaturalPersonClient();
        client.setIdentification(entity.getIdentification());
        client.setFullName(entity.getFullName());
        client.setBirthDate(entity.getBirthDate());
        client.setEmail(entity.getEmail());
        client.setPhoneNumber(entity.getPhoneNumber());
        client.setAddress(entity.getAddress());
        client.setClientStatus(entity.getClientStatus() != null
                ? ClientStatus.valueOf(entity.getClientStatus()) : null);
        return client;
    }

    // -------------------------------------------------------------------------
    // Mapeo entity ↔ modelo – Empresa
    // -------------------------------------------------------------------------

    private CompanyClientEntity toCompanyEntity(CompanyClient company) {
        CompanyClientEntity entity = new CompanyClientEntity();
        entity.setNit(company.getNit());
        entity.setCompanyName(company.getCompanyName());
        entity.setEmail(company.getEmail());
        entity.setPhoneNumber(company.getPhoneNumber());
        entity.setAddress(company.getAddress());
        entity.setClientStatus(company.getClientStatus() != null
                ? company.getClientStatus().name() : null);
        // Representante legal: se guarda como referencia (stub con ID) siguiendo
        // el patrón del proyecto clínica (InvoicePersistenceAdapter usa stub con ID).
        if (company.getLegalRepresentative() != null) {
            NaturalPersonClientEntity legalRepStub = new NaturalPersonClientEntity();
            legalRepStub.setIdentification(company.getLegalRepresentative().getIdentification());
            entity.setLegalRepresentative(legalRepStub);
        }
        return entity;
    }

    private CompanyClient toCompanyModel(CompanyClientEntity entity) {
        if (entity == null) return null;
        CompanyClient company = new CompanyClient();
        company.setNit(entity.getNit());
        company.setCompanyName(entity.getCompanyName());
        company.setEmail(entity.getEmail());
        company.setPhoneNumber(entity.getPhoneNumber());
        company.setAddress(entity.getAddress());
        company.setClientStatus(entity.getClientStatus() != null
                ? ClientStatus.valueOf(entity.getClientStatus()) : null);
        // Representante legal: se reconstruye desde la entity relacionada (JPA ya hizo el join)
        if (entity.getLegalRepresentative() != null) {
            company.setLegalRepresentative(toNaturalPersonModel(entity.getLegalRepresentative()));
        }
        return company;
    }
}
