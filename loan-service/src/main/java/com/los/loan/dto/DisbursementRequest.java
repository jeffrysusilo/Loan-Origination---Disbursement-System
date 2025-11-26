package com.los.loan.dto;

import com.los.loan.entity.Disbursement;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisbursementRequest {

    @NotNull(message = "Disbursement method is required")
    private Disbursement.DisbursementMethod disbursementMethod;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Bank code is required")
    private String bankCode;
}
