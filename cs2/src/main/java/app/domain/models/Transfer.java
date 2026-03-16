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


public class Transfer {
    private int transfer_id;
    private String origin_account;
    private String destination_account;
    private double amount;
    private Date creation_date;
    private Date approval_date;
    private String transfer_status;
    private int creator_user_id;
    private int approver_user_id;
}
