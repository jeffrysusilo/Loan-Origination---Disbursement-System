package com.los.loan.dto;

import com.los.loan.entity.Loan;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Long id;
    private Long customerId;
    private Long productId;
    private String productName;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private Integer tenor;
    private BigDecimal downPayment;
    private BigDecimal interestRate;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalPayment;
    private String purpose;
    private Loan.LoanStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime disbursedAt;
}
