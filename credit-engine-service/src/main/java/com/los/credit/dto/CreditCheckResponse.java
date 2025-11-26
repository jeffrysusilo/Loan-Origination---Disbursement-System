package com.los.credit.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCheckResponse {
    private Long id;
    private Long customerId;
    private Long loanId;
    private BigDecimal monthlyIncome;
    private BigDecimal existingDebt;
    private BigDecimal requestedAmount;
    private BigDecimal dtiRatio;
    private Integer creditScore;
    private String decision; // APPROVED, REJECTED, REVIEW
    private String remarks;
    private LocalDateTime checkedAt;
}
