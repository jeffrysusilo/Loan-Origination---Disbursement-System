package com.los.credit.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCheckRequest {
    private Long customerId;
    private Long loanId;
    private BigDecimal requestedAmount;
    private BigDecimal monthlyIncome;
    private BigDecimal existingDebt;
}
