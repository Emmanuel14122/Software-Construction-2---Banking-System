package app.application.adapters.persistence.sql.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "loans")
@Getter
@Setter

public class LoanEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @Column(name = "loan_type")
    private String loanType;

    @Column(name = "client_requestor_id")
    private String clientRequestorId;

    @Column(name = "requested_amount")
    private BigDecimal requestedAmount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "term_months")
    private int termMonths;

    @Column(name = "loan_status")
    private String loanStatus;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "destination_account_disbursement")
    private String destinationAccountDisbursement;

    @Column(name = "approver_analyst_id")
    private Long approverAnalystId;
}
