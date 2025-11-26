package com.los.loan.dto;

import com.los.loan.entity.Approval;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "Approver role is required")
    private Approval.ApprovalLevel approverRole;

    @NotNull(message = "Decision is required")
    private Approval.ApprovalDecision decision;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    private String approverName;
}
