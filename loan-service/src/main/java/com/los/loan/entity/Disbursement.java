package com.los.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "disbursements", schema = "loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DisbursementMethod disbursementMethod;

    @Column(length = 50)
    private String accountNumber;

    @Column(length = 100)
    private String accountName;

    @Column(length = 20)
    private String bankCode;

    @Column(length = 500)
    private String spkFilePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisbursementStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime disbursedAt;

    public enum DisbursementMethod {
        BANK_TRANSFER,
        CASH,
        CHECK
    }

    public enum DisbursementStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
