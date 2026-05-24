package app.application.adapters.persistence.sql.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.application.adapters.persistence.sql.entities.LoanEntity;
import app.application.adapters.persistence.sql.repositories.LoanRepository;
import app.domain.models.Loan;
import app.domain.models.enums.LoanStatus;
import app.domain.models.enums.LoanType;
import app.domain.ports.LoanPort;

@Service
public class LoanPersistenceAdapter implements LoanPort{

    private final LoanRepository loanRepository;

    public LoanPersistenceAdapter(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }


    @Override
    public Optional<Loan> findById(Long loanId) {
        return loanRepository.findById(loanId).map(this::toModel);
    }

    @Override
    public List<Loan> findByClient(String clientId) {
        return loanRepository.findByClientRequestorId(clientId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findByClientAndStatus(String clientId, LoanStatus status) {
        return loanRepository.findByClientRequestorIdAndLoanStatus(clientId, status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findByStatus(LoanStatus status) {
        return loanRepository.findByLoanStatus(status.name()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }


    @Override
    public boolean existsById(Long loanId) {
        return loanRepository.existsById(loanId);
    }


    @Override
    public void save(Loan loan) {
        loanRepository.save(toEntity(loan));
    }

    @Override
    public void update(Loan loan) {
        loanRepository.findById(loan.getLoanId()).ifPresent(existing -> {
            existing.setLoanStatus(loan.getLoanStatus() != null
                    ? loan.getLoanStatus().name() : null);
            existing.setApprovedAmount(loan.getApprovedAmount());
            existing.setInterestRate(loan.getInterestRate());
            existing.setApprovalDate(loan.getApprovalDate());
            existing.setDisbursementDate(loan.getDisbursementDate());
            existing.setDestinationAccountDisbursement(loan.getDestinationAccountDisbursement());
            existing.setApproverAnalystId(loan.getApproverAnalystId());
            loanRepository.save(existing);
        });
    }


    private LoanEntity toEntity(Loan loan) {
        LoanEntity entity = new LoanEntity();
        entity.setLoanType(loan.getLoanType() != null ? loan.getLoanType().name() : null);
        entity.setClientRequestorId(loan.getClientRequestorId());
        entity.setRequestedAmount(loan.getRequestedAmount());
        entity.setApprovedAmount(loan.getApprovedAmount());
        entity.setInterestRate(loan.getInterestRate());
        entity.setTermMonths(loan.getTermMonths());
        entity.setLoanStatus(loan.getLoanStatus() != null ? loan.getLoanStatus().name() : null);
        entity.setApprovalDate(loan.getApprovalDate());
        entity.setDisbursementDate(loan.getDisbursementDate());
        entity.setDestinationAccountDisbursement(loan.getDestinationAccountDisbursement());
        entity.setApproverAnalystId(loan.getApproverAnalystId());
        return entity;
    }

    private Loan toModel(LoanEntity entity) {
        if (entity == null) return null;
        Loan loan = new Loan();
        loan.setLoanId(entity.getLoanId());
        loan.setLoanType(entity.getLoanType() != null
                ? LoanType.valueOf(entity.getLoanType()) : null);
        loan.setClientRequestorId(entity.getClientRequestorId());
        loan.setRequestedAmount(entity.getRequestedAmount());
        loan.setApprovedAmount(entity.getApprovedAmount());
        loan.setInterestRate(entity.getInterestRate());
        loan.setTermMonths(entity.getTermMonths());
        loan.setLoanStatus(entity.getLoanStatus() != null
                ? LoanStatus.valueOf(entity.getLoanStatus()) : null);
        loan.setApprovalDate(entity.getApprovalDate());
        loan.setDisbursementDate(entity.getDisbursementDate());
        loan.setDestinationAccountDisbursement(entity.getDestinationAccountDisbursement());
        loan.setApproverAnalystId(entity.getApproverAnalystId());
        return loan;
    }
}
