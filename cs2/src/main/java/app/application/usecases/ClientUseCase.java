package app.application.usecases;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import app.domain.Exceptions.BusinessException;
import app.domain.models.CompanyClient;
import app.domain.models.NaturalPersonClient;
import app.domain.services.client.SaveNaturalPersonClient;
import app.domain.services.client.UpdateNaturalPersonClient;
import app.domain.services.client.DeleteNaturalPersonClient;
import app.domain.services.client.FindNaturalPersonClient;
import app.domain.services.client.ExistsNaturalPersonClient;
import app.domain.services.client.SaveCompanyClient;
import app.domain.services.client.UpdateCompanyClient;
import app.domain.services.client.DeleteCompanyClient;
import app.domain.services.client.FindCompanyClient;
import app.domain.services.client.ExistsCompanyClient;
import app.domain.services.client.ExistsClientByEmail;
 
import java.util.List;
 
@Service
public class ClientUseCase implements app.domain.ports.ClientPort {
 
    @Autowired
    private SaveNaturalPersonClient saveNaturalPersonClient;
    @Autowired
    private UpdateNaturalPersonClient updateNaturalPersonClient;
    @Autowired
    private DeleteNaturalPersonClient deleteNaturalPersonClient;
    @Autowired
    private FindNaturalPersonClient findNaturalPersonClient;
    @Autowired
    private ExistsNaturalPersonClient existsNaturalPersonClient;
    @Autowired
    private SaveCompanyClient saveCompanyClient;
    @Autowired
    private UpdateCompanyClient updateCompanyClient;
    @Autowired
    private DeleteCompanyClient deleteCompanyClient;
    @Autowired
    private FindCompanyClient findCompanyClient;
    @Autowired
    private ExistsCompanyClient existsCompanyClient;
    @Autowired
    private ExistsClientByEmail existsClientByEmail;
 
    public ClientUseCase(SaveNaturalPersonClient saveNaturalPersonClient,
                         UpdateNaturalPersonClient updateNaturalPersonClient,
                         DeleteNaturalPersonClient deleteNaturalPersonClient,
                         FindNaturalPersonClient findNaturalPersonClient,
                         ExistsNaturalPersonClient existsNaturalPersonClient,
                         SaveCompanyClient saveCompanyClient,
                         UpdateCompanyClient updateCompanyClient,
                         DeleteCompanyClient deleteCompanyClient,
                         FindCompanyClient findCompanyClient,
                         ExistsCompanyClient existsCompanyClient,
                         ExistsClientByEmail existsClientByEmail) {
        this.saveNaturalPersonClient = saveNaturalPersonClient;
        this.updateNaturalPersonClient = updateNaturalPersonClient;
        this.deleteNaturalPersonClient = deleteNaturalPersonClient;
        this.findNaturalPersonClient = findNaturalPersonClient;
        this.existsNaturalPersonClient = existsNaturalPersonClient;
        this.saveCompanyClient = saveCompanyClient;
        this.updateCompanyClient = updateCompanyClient;
        this.deleteCompanyClient = deleteCompanyClient;
        this.findCompanyClient = findCompanyClient;
        this.existsCompanyClient = existsCompanyClient;
        this.existsClientByEmail = existsClientByEmail;
    }
 
    @Override
    public void saveNaturalPerson(NaturalPersonClient client) throws BusinessException {
        saveNaturalPersonClient.saveNaturalPerson(client);
    }
 
    @Override
    public void updateNaturalPerson(NaturalPersonClient client) throws BusinessException {
        updateNaturalPersonClient.updateNaturalPerson(client);
    }
 
    @Override
    public void deleteNaturalPersonByDocument(String identification) throws BusinessException {
        deleteNaturalPersonClient.deleteNaturalPersonByDocument(identification);
    }
 
    @Override
    public NaturalPersonClient findNaturalPersonByDocument(String identification) throws BusinessException {
        return findNaturalPersonClient.findNaturalPersonByDocument(identification);
    }
 
    @Override
    public List<NaturalPersonClient> findAllNaturalPersons() {
        return findNaturalPersonClient.findAllNaturalPersons();
    }
 
    @Override
    public boolean existsNaturalPersonByDocument(String identification) {
        return existsNaturalPersonClient.existsNaturalPersonByDocument(identification);
    }
 
    @Override
    public boolean existsByEmail(String email) {
        return existsClientByEmail.existsByEmail(email);
    }
 
    @Override
    public void saveCompany(CompanyClient company) throws BusinessException {
        saveCompanyClient.saveCompany(company);
    }
 
    @Override
    public void updateCompany(CompanyClient company) throws BusinessException {
        updateCompanyClient.updateCompany(company);
    }
 
    @Override
    public void deleteCompanyByNit(String nit) throws BusinessException {
        deleteCompanyClient.deleteCompanyByNit(nit);
    }
 
    @Override
    public CompanyClient findCompanyByNit(String nit) throws BusinessException {
        return findCompanyClient.findCompanyByNit(nit);
    }
 
    @Override
    public List<CompanyClient> findAllCompanies() {
        return findCompanyClient.findAllCompanies();
    }
 
    @Override
    public boolean existsCompanyByNit(String nit) {
        return existsCompanyClient.existsCompanyByNit(nit);
    }
 
}
 