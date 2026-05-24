package app.application.persistence.sql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.application.persistence.sql.entities.LoanEntity;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    
    List<LoanEntity> findByClientRequestorId(String clientRequestorId);

    List<LoanEntity> findByClientRequestorIdAndLoanStatus(String clientRequestorId, String loanStatus);

    List<LoanEntity> findByLoanStatus(String loanStatus);
}
