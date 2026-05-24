package app.application.adapters.persistence.sql.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.application.adapters.persistence.sql.entities.GeneralBankingProductEntity;
import app.application.adapters.persistence.sql.repositories.GeneralBankingProductRepository;
import app.domain.models.GeneralBankingProduct;


@Service
public class GeneralBankingProductPersistenceAdapter {
        private final GeneralBankingProductRepository repository;

    public GeneralBankingProductPersistenceAdapter(GeneralBankingProductRepository repository) {
        this.repository = repository;
    }

    public void save(GeneralBankingProduct product) {
        repository.save(toEntity(product));
    }

    public Optional<GeneralBankingProduct> findByProductCode(String productCode) {
        return repository.findById(productCode).map(this::toModel);
    }

    public List<GeneralBankingProduct> findAll() {
        return repository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }



    private GeneralBankingProductEntity toEntity(GeneralBankingProduct product) {
        GeneralBankingProductEntity entity = new GeneralBankingProductEntity();
        entity.setProductCode(product.getProductCode());
        entity.setProductName(product.getProductName());
        entity.setCategory(product.getCategory());
        entity.setRequiresApproval(product.isRequiresApproval());
        return entity;
    }

    private GeneralBankingProduct toModel(GeneralBankingProductEntity entity) {
        if (entity == null) return null;
        GeneralBankingProduct product = new GeneralBankingProduct();
        product.setProductCode(entity.getProductCode());
        product.setProductName(entity.getProductName());
        product.setCategory(entity.getCategory());
        product.setRequiresApproval(entity.isRequiresApproval());
        return product;
    }

}
