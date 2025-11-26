package com.los.credit.service;

import com.los.credit.dto.CreditCheckRequest;
import com.los.credit.dto.CreditCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
public class CreditEngineService {

    private static final BigDecimal DTI_THRESHOLD_AUTO_REJECT = new BigDecimal("40.00");
    private static final BigDecimal DTI_THRESHOLD_REVIEW = new BigDecimal("30.00");
    private static final BigDecimal HIGH_INCOME_THRESHOLD = new BigDecimal("20000000");

    public CreditCheckResponse performCreditCheck(CreditCheckRequest request) {
        log.info("Performing credit check for customer: {}", request.getCustomerId());

        // Calculate DTI (Debt-to-Income Ratio)
        BigDecimal dtiRatio = calculateDTI(request);

        // Calculate credit score (simplified)
        Integer creditScore = calculateCreditScore(request, dtiRatio);

        // Make decision
        String decision = makeDecision(dtiRatio, creditScore, request.getMonthlyIncome());
        String remarks = generateRemarks(dtiRatio, creditScore, decision);

        log.info("Credit check completed - DTI: {}%, Score: {}, Decision: {}", 
                dtiRatio, creditScore, decision);

        return CreditCheckResponse.builder()
                .id(System.currentTimeMillis()) // Simulated ID
                .customerId(request.getCustomerId())
                .loanId(request.getLoanId())
                .monthlyIncome(request.getMonthlyIncome())
                .existingDebt(request.getExistingDebt())
                .requestedAmount(request.getRequestedAmount())
                .dtiRatio(dtiRatio)
                .creditScore(creditScore)
                .decision(decision)
                .remarks(remarks)
                .checkedAt(LocalDateTime.now())
                .build();
    }

    private BigDecimal calculateDTI(CreditCheckRequest request) {
        // Calculate monthly installment (simplified - assume 24 months, 12% interest)
        BigDecimal monthlyRate = new BigDecimal("0.01"); // 1% per month
        int tenor = 24;
        
        BigDecimal principal = request.getRequestedAmount();
        BigDecimal numerator = principal.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(
                        BigDecimal.ONE.add(monthlyRate).pow(tenor), 10, RoundingMode.HALF_UP
                )
        );
        BigDecimal monthlyInstallment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        // DTI = (existing debt + new installment) / monthly income
        BigDecimal totalDebt = request.getExistingDebt().add(monthlyInstallment);
        BigDecimal dti = totalDebt.divide(request.getMonthlyIncome(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return dti.setScale(2, RoundingMode.HALF_UP);
    }

    private Integer calculateCreditScore(CreditCheckRequest request, BigDecimal dtiRatio) {
        int score = 700; // Base score

        // DTI impact (-200 to +100)
        if (dtiRatio.compareTo(new BigDecimal("10")) < 0) {
            score += 100;
        } else if (dtiRatio.compareTo(new BigDecimal("20")) < 0) {
            score += 50;
        } else if (dtiRatio.compareTo(new BigDecimal("30")) < 0) {
            score += 20;
        } else if (dtiRatio.compareTo(new BigDecimal("40")) < 0) {
            score -= 50;
        } else {
            score -= 200;
        }

        // Income level impact (0 to +100)
        if (request.getMonthlyIncome().compareTo(HIGH_INCOME_THRESHOLD) >= 0) {
            score += 100;
        } else if (request.getMonthlyIncome().compareTo(new BigDecimal("10000000")) >= 0) {
            score += 50;
        }

        // Existing debt impact (-100 to 0)
        if (request.getExistingDebt().compareTo(request.getMonthlyIncome()) > 0) {
            score -= 100;
        } else if (request.getExistingDebt().compareTo(request.getMonthlyIncome().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)) > 0) {
            score -= 50;
        }

        // Ensure score is in valid range
        score = Math.max(300, Math.min(850, score));

        return score;
    }

    private String makeDecision(BigDecimal dtiRatio, Integer creditScore, BigDecimal monthlyIncome) {
        // Auto reject conditions
        if (dtiRatio.compareTo(DTI_THRESHOLD_AUTO_REJECT) > 0) {
            return "REJECTED";
        }
        if (creditScore < 500) {
            return "REJECTED";
        }

        // Auto approve conditions (low risk)
        if (dtiRatio.compareTo(new BigDecimal("20")) < 0 && 
            creditScore >= 750 && 
            monthlyIncome.compareTo(HIGH_INCOME_THRESHOLD) >= 0) {
            return "APPROVED";
        }

        // Manual review needed
        if (dtiRatio.compareTo(DTI_THRESHOLD_REVIEW) > 0 || creditScore < 650) {
            return "REVIEW";
        }

        // Default to review for safety
        return "REVIEW";
    }

    private String generateRemarks(BigDecimal dtiRatio, Integer creditScore, String decision) {
        StringBuilder remarks = new StringBuilder();

        remarks.append("DTI Ratio: ").append(dtiRatio).append("%. ");
        remarks.append("Credit Score: ").append(creditScore).append(". ");

        switch (decision) {
            case "APPROVED":
                remarks.append("Low risk profile. Auto-approved.");
                break;
            case "REJECTED":
                if (dtiRatio.compareTo(DTI_THRESHOLD_AUTO_REJECT) > 0) {
                    remarks.append("DTI ratio exceeds maximum threshold (40%).");
                } else {
                    remarks.append("Credit score below minimum requirement.");
                }
                break;
            case "REVIEW":
                remarks.append("Manual review required. Medium risk profile.");
                break;
        }

        return remarks.toString();
    }
}
