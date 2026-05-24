package app.application.adapters.persistence.sql.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.application.adapters.persistence.sql.repositories.BankAccountRepository;
import app.application.adapters.persistence.sql.repositories.TransferRepository;
import app.domain.models.Transfer;
import app.domain.models.enums.TransferStatus;
import app.domain.ports.TransferPort;

@Service
public class TransferPersistenceAdapter implements TransferPort{
    private final TransferRepository transferRepository;
    private final BankAccountRepository bankAccountRepository;

    public TransferPersistenceAdapter(TransferRepository transferRepository,
                                       BankAccountRepository bankAccountRepository) {
        this.transferRepository = transferRepository;
        this.bankAccountRepository = bankAccountRepository;
    }


    @Override
    public Optional<Transfer> findById(Long transferId) {
        return transferRepository.findById(transferId).map(this::toModel);
    }

    @Override
    public List<Transfer> findByAccount(String accountNumber) {
        return transferRepository
                .findByOriginAccountOrDestinationAccount(accountNumber, accountNumber).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByOriginAccount(String originAccount) {
        return transferRepository.findByOriginAccount(originAccount).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByStatus(TransferStatus status) {
        return transferRepository.findByTransferStatus(status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByCreatorUser(Long creatorUserId) {
        return transferRepository.findByCreatorUserId(creatorUserId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByCompanyAndStatus(String companyNit, TransferStatus status) {
        List<String> companyAccountNumbers = bankAccountRepository
                .findByAccountHolderId(companyNit).stream()
                .map(entity -> entity.getAccountNumber())
                .collect(Collectors.toList());

        if (companyAccountNumbers.isEmpty()) {
            return List.of();
        }

        return transferRepository
                .findByOriginAccountInAndTransferStatus(companyAccountNumbers, status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findWaitingApprovalExpiredBefore(LocalDateTime now) {
        return transferRepository
                .findByTransferStatusAndExpirationCheckAtBefore(
                        TransferStatus.WaitingApproval.name(), now).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findAll() {
        return transferRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }


    @Override
    public boolean existsById(Long transferId) {
        return transferRepository.existsById(transferId);
    }

    @Override
    public void save(Transfer transfer) {
        transferRepository.save(toEntity(transfer));
    }

    @Override
    public void update(Transfer transfer) {
        transferRepository.findById(transfer.getTransferId()).ifPresent(existing -> {
            existing.setTransferStatus(transfer.getTransferStatus() != null
                    ? transfer.getTransferStatus().name() : null);
            existing.setApprovalDate(transfer.getApprovalDate());
            existing.setApproverUserId(transfer.getApproverUserId());
            existing.setExpirationCheckAt(transfer.getExpirationCheckAt());
            transferRepository.save(existing);
        });
    }


    private TransferEntity toEntity(Transfer transfer) {
        TransferEntity entity = new TransferEntity();
        entity.setOriginAccount(transfer.getOriginAccount());
        entity.setDestinationAccount(transfer.getDestinationAccount());
        entity.setAmount(transfer.getAmount());
        entity.setCreationDate(transfer.getCreationDate());
        entity.setApprovalDate(transfer.getApprovalDate());
        entity.setTransferStatus(transfer.getTransferStatus() != null
                ? transfer.getTransferStatus().name() : null);
        entity.setCreatorUserId(transfer.getCreatorUserId());
        entity.setApproverUserId(transfer.getApproverUserId());
        entity.setExpirationCheckAt(transfer.getExpirationCheckAt());
        return entity;
    }

    private Transfer toModel(TransferEntity entity) {
        if (entity == null) return null;
        Transfer transfer = new Transfer();
        transfer.setTransferId(entity.getTransferId());
        transfer.setOriginAccount(entity.getOriginAccount());
        transfer.setDestinationAccount(entity.getDestinationAccount());
        transfer.setAmount(entity.getAmount());
        transfer.setCreationDate(entity.getCreationDate());
        transfer.setApprovalDate(entity.getApprovalDate());
        transfer.setTransferStatus(entity.getTransferStatus() != null
                ? TransferStatus.valueOf(entity.getTransferStatus()) : null);
        transfer.setCreatorUserId(entity.getCreatorUserId());
        transfer.setApproverUserId(entity.getApproverUserId());
        transfer.setExpirationCheckAt(entity.getExpirationCheckAt());
        return transfer;
    }
}
