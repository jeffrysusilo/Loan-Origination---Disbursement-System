package com.los.loan.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenor is required")
    @Min(value = 1, message = "Tenor must be at least 1 month")
    @Max(value = 120, message = "Tenor cannot exceed 120 months")
    private Integer tenor;

    @NotNull(message = "Down payment is required")
    @DecimalMin(value = "0.0", message = "Down payment cannot be negative")
    private BigDecimal downPayment;

    @NotBlank(message = "Purpose is required")
    @Size(max = 500, message = "Purpose must not exceed 500 characters")
    private String purpose;
}
