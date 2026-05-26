package app.application.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.application.api.request.CompanyClientRequest;
import app.application.api.request.NaturalPersonClientRequest;
import app.application.api.response.CompanyClientResponse;
import app.application.api.response.NaturalPersonClientResponse;
import app.domain.models.CompanyClient;
import app.domain.models.NaturalPersonClient;
import app.domain.services.ClientService;
import app.domain.services.UserSystemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {
    
    private final ClientService clientService;
    private final UserSystemService userSystemService;

    public ClientController(ClientService clientService, UserSystemService userSystemService) {
        this.clientService = clientService;
        this.userSystemService = userSystemService;
    }

    //  ── ANALISTA INTERNO ──────────────────────────────────────────────────────────────

    /**
     * GET /internal_analyst/clients/natural-persons
     * Lista todas las personas naturales registradas.
     */
    @GetMapping("/internal_analyst/clients/natural-persons")
    public ResponseEntity<List<NaturalPersonClientResponse>> getAllNaturalPersons() {
        List<NaturalPersonClientResponse> response = clientService.getAllNaturalPersons()
                .stream()
                .map(ClientController::toNaturalPersonResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/clients/natural-persons/{identification}
     * Consulta una persona natural por su cédula/DNI.
     */
    @GetMapping("/internal_analyst/clients/natural-persons/{identification}")
    public ResponseEntity<NaturalPersonClientResponse> getNaturalPerson(
            @PathVariable String identification) {
        NaturalPersonClient client = clientService.getNaturalPersonByDocument(identification);
        return ResponseEntity.ok(toNaturalPersonResponse(client));
    }

    /**
     * GET /internal_analyst/clients/companies
     * Lista todas las empresas registradas.
     */
    @GetMapping("/internal_analyst/clients/companies")
    public ResponseEntity<List<CompanyClientResponse>> getAllCompanies() {
        List<CompanyClientResponse> response = clientService.getAllCompanies()
                .stream()
                .map(ClientController::toCompanyResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /internal_analyst/clients/companies/{nit}
     * Consulta una empresa por su NIT.
     */
    @GetMapping("/internal_analyst/clients/companies/{nit}")
    public ResponseEntity<CompanyClientResponse> getCompany(@PathVariable String nit) {
        CompanyClient company = clientService.getCompanyByNit(nit);
        return ResponseEntity.ok(toCompanyResponse(company));
    }

    
    // ── EMPLEADO COMERCIAL ──────────────────────────────────────────────────────────────

    /**
     * POST /sales_employe/clients/natural-persons
     * Registra una nueva persona natural como cliente del banco.
     */
    @PostMapping("/sales_employe/clients/natural-persons")
    public ResponseEntity<NaturalPersonClientResponse> registerNaturalPerson(
            @Valid @RequestBody NaturalPersonClientRequest request) {
        NaturalPersonClient client = toNaturalPersonModel(request);
        clientService.registerNaturalPerson(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(toNaturalPersonResponse(client));
    }

    /**
     * POST /sales_employe/clients/companies
     * Registra una nueva empresa como cliente del banco.
     */
    @PostMapping("/sales_employe/clients/companies")
    public ResponseEntity<CompanyClientResponse> registerCompany(
            @Valid @RequestBody CompanyClientRequest request) {
        CompanyClient company = toCompanyModel(request);
        clientService.registerCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCompanyResponse(company));
    }

    /**
     * GET /sales_employe/clients/natural-persons/{identification}
     * Consulta información completa de una persona natural bajo su gestión.
     */
    @GetMapping("/sales_employe/clients/natural-persons/{identification}")
    public ResponseEntity<NaturalPersonClientResponse> getNaturalPersonForSales(
            @PathVariable String identification) {
        NaturalPersonClient client = clientService.getNaturalPersonByDocument(identification);
        return ResponseEntity.ok(toNaturalPersonResponse(client));
    }

    /**
     * GET /sales_employe/clients/companies/{nit}
     * Consulta información completa de una empresa bajo su gestión.
     */
    @GetMapping("/sales_employe/clients/companies/{nit}")
    public ResponseEntity<CompanyClientResponse> getCompanyForSales(@PathVariable String nit) {
        CompanyClient company = clientService.getCompanyByNit(nit);
        return ResponseEntity.ok(toCompanyResponse(company));
    }

    /**
     * PUT /sales_employe/clients/natural-persons/{identification}
     * Actualiza los datos de una persona natural.
     */
    @PutMapping("/sales_employe/clients/natural-persons/{identification}")
    public ResponseEntity<NaturalPersonClientResponse> updateNaturalPerson(
            @PathVariable String identification,
            @Valid @RequestBody NaturalPersonClientRequest request) {
        request.setIdentification(identification);
        NaturalPersonClient client = toNaturalPersonModel(request);
        clientService.updateNaturalPerson(client);
        return ResponseEntity.ok(toNaturalPersonResponse(client));
    }

    /**
     * PUT /sales_employe/clients/companies/{nit}
     * Actualiza los datos de una empresa.
     */
    @PutMapping("/sales_employe/clients/companies/{nit}")
    public ResponseEntity<CompanyClientResponse> updateCompany(
            @PathVariable String nit,
            @Valid @RequestBody CompanyClientRequest request) {
        request.setNit(nit);
        CompanyClient company = toCompanyModel(request);
        clientService.updateCompany(company);
        return ResponseEntity.ok(toCompanyResponse(company));
    }


    // ── EMPLEADO DE VENTANILLA ──────────────────────────────────────────────────────

    /**
     * POST /window_employe/clients/natural-persons
     * Registra una persona natural (flujo de ventanilla para abrir cuenta).
     */
    @PostMapping("/window_employe/clients/natural-persons")
    public ResponseEntity<NaturalPersonClientResponse> registerNaturalPersonAtWindow(
            @Valid @RequestBody NaturalPersonClientRequest request) {
        NaturalPersonClient client = toNaturalPersonModel(request);
        clientService.registerNaturalPerson(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(toNaturalPersonResponse(client));
    }

    /**
     * GET /window_employe/clients/natural-persons/{identification}
     * Consulta datos básicos del cliente para verificar identidad en ventanilla.
     */
    @GetMapping("/window_employe/clients/natural-persons/{identification}")
    public ResponseEntity<NaturalPersonClientResponse> getNaturalPersonAtWindow(
            @PathVariable String identification) {
        NaturalPersonClient client = clientService.getNaturalPersonByDocument(identification);
        return ResponseEntity.ok(toNaturalPersonResponse(client));
    }


    // ── CLIENTE PERSONA NATURAL ──────────────────────────────────────────────────────────────

    /**
     * GET /person_customer_user/profile
     * El cliente persona natural consulta su propio perfil.
     * El documento se extrae del token JWT (authentication.getDetails()).
     */
    @GetMapping("/person_customer_user/profile")
    public ResponseEntity<NaturalPersonClientResponse> getOwnProfile(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        NaturalPersonClient client = clientService.getNaturalPersonByDocument(identification);
        return ResponseEntity.ok(toNaturalPersonResponse(client));
    }

    // ── CLIENTE EMPRESA ──────────────────────────────────────────────────────────────

    /**
     * GET /corporate_customer_user/profile
     * El cliente empresa consulta el perfil de su empresa.
     * El NIT se resuelve a través del usuario autenticado.
     */
    @GetMapping("/corporate_customer_user/profile")
    public ResponseEntity<CompanyClientResponse> getOwnCompanyProfile(Authentication authentication) {
        String identification = (String) authentication.getDetails();
        var user = userSystemService.getUserByDocument(identification);
        String nit = user.getRelatedClientId() != null
                ? ((app.domain.models.CompanyClient) user.getRelatedClientId()).getNit()
                : identification;
        CompanyClient company = clientService.getCompanyByNit(nit);
        return ResponseEntity.ok(toCompanyResponse(company));
    }

    // =========================================================================
    // Mappers request / modelo de dominio
    // =========================================================================

    private static NaturalPersonClient toNaturalPersonModel(NaturalPersonClientRequest req) {
        NaturalPersonClient client = new NaturalPersonClient();
        client.setIdentification(req.getIdentification());
        client.setFullName(req.getFullName());
        client.setBirthDate(req.getBirthDate());
        client.setEmail(req.getEmail());
        client.setPhoneNumber(req.getPhoneNumber());
        client.setAddress(req.getAddress());
        return client;
    }

    private static CompanyClient toCompanyModel(CompanyClientRequest req) {
        CompanyClient company = new CompanyClient();
        company.setNit(req.getNit());
        company.setCompanyName(req.getCompanyName());
        company.setEmail(req.getEmail());
        company.setPhoneNumber(req.getPhoneNumber());
        company.setAddress(req.getAddress());
        // El representante legal se pasa como referencia; el servicio lo valida y resuelve
        if (req.getLegalRepresentativeId() != null) {
            NaturalPersonClient legalRep = new NaturalPersonClient();
            legalRep.setIdentification(req.getLegalRepresentativeId());
            company.setLegalRepresentative(legalRep);
        }
        return company;
    }

    // =========================================================================
    // Mappers modelo de dominio / response
    // =========================================================================

    private static NaturalPersonClientResponse toNaturalPersonResponse(NaturalPersonClient client) {
        return new NaturalPersonClientResponse(
                client.getFullName(),
                client.getIdentification(),
                client.getBirthDate(),
                client.getAddress(),
                client.getPhoneNumber(),
                client.getEmail()
        );
    }

    private static CompanyClientResponse toCompanyResponse(CompanyClient company) {
        String legalRepId = company.getLegalRepresentative() != null
                ? company.getLegalRepresentative().getIdentification()
                : null;
        return new CompanyClientResponse(
                company.getNit(),
                company.getCompanyName(),
                company.getEmail(),
                company.getPhoneNumber(),
                company.getAddress(),
                legalRepId
        );
    }
}
