package app.domain.models;

import java.util.Date;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Loan {
    private int loan_id;
    private String type_loan;
    private String client_requestor_id;
    private double requesting_amount;
    private double approved_amount;
    private double interest_rate;
    private int term_months;
    private String loan_status;
    private Date approval_date;
    private Date disbursement_date;
    private String destination_account_disbursement;

}
