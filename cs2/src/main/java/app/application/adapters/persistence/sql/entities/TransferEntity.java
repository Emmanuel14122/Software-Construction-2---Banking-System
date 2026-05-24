package app.application.adapters.persistence.sql.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transfers")
@Getter
@Setter

public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    @Column(name = "origin_account", nullable = false)
    private String originAccount;

    @Column(name = "destination_account", nullable = false)
    private String destinationAccount;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "transfer_status", nullable = false)
    private String transferStatus;

    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId;

    @Column(name = "approver_user_id", nullable = false)
    private Long approverUserId;
    
    @Column(name = "expiration_check_at")
    private LocalDateTime expirationCheckAt;
    
}
