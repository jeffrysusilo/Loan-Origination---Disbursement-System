package com.los.loan.service;

import com.los.loan.dto.*;
import com.los.loan.entity.*;
import com.los.loan.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanProductRepository productRepository;
    private final ApprovalRepository approvalRepository;
    private final DisbursementRepository disbursementRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        log.info("Processing loan application for customer: {}", request.getCustomerId());

        // Validate product exists
        LoanProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate amount and tenor
        validateLoanRequest(request, product);

        // Calculate interest and installment
        BigDecimal interestRate = product.getInterestRate();
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);

        BigDecimal loanAmount = request.getRequestedAmount().subtract(request.getDownPayment());
        BigDecimal monthlyInstallment = calculateMonthlyInstallment(loanAmount, monthlyRate, request.getTenor());
        BigDecimal totalPayment = monthlyInstallment.multiply(BigDecimal.valueOf(request.getTenor()));

        // Create loan
        Loan loan = Loan.builder()
                .customerId(request.getCustomerId())
                .product(product)
                .requestedAmount(request.getRequestedAmount())
                .approvedAmount(null) // Will be set after approval
                .tenor(request.getTenor())
                .downPayment(request.getDownPayment())
                .interestRate(interestRate)
                .monthlyInstallment(monthlyInstallment)
                .totalPayment(totalPayment)
                .purpose(request.getPurpose())
                .status(Loan.LoanStatus.PENDING)
                .build();

        Loan savedLoan = loanRepository.save(loan);

        // Create initial approval records
        createInitialApprovals(savedLoan);

        // Publish event
        kafkaTemplate.send("loan-events", "LOAN_APPLIED:" + savedLoan.getId());

        log.info("Loan application created with ID: {}", savedLoan.getId());
        return mapToResponse(savedLoan);
    }

    @Transactional
    public LoanResponse approveLoan(Long loanId, ApprovalRequest request) {
        log.info("Processing approval for loan: {} by {}", loanId, request.getApproverRole());

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        Approval approval = approvalRepository.findByLoanIdAndApprovalLevel(loanId, request.getApproverRole())
                .orElseThrow(() -> new RuntimeException("Approval level not found"));

        approval.setDecision(request.getDecision());
        approval.setNotes(request.getNotes());
        approval.setApproverName(request.getApproverName());
        approvalRepository.save(approval);

        // Update loan status based on decision
        if (request.getDecision() == Approval.ApprovalDecision.REJECTED) {
            loan.setStatus(Loan.LoanStatus.REJECTED);
            loan.setRemarks(request.getNotes());
            kafkaTemplate.send("loan-events", "LOAN_REJECTED:" + loanId);
        } else if (isFullyApproved(loanId)) {
            loan.setStatus(Loan.LoanStatus.APPROVED);
            loan.setApprovedAmount(loan.getRequestedAmount());
            loan.setApprovedAt(LocalDateTime.now());
            kafkaTemplate.send("loan-events", "LOAN_APPROVED:" + loanId);
        } else {
            loan.setStatus(Loan.LoanStatus.UNDER_REVIEW);
        }

        Loan savedLoan = loanRepository.save(loan);
        return mapToResponse(savedLoan);
    }

    @Transactional
    public void disburseLoan(Long loanId, DisbursementRequest request) {
        log.info("Processing disbursement for loan: {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != Loan.LoanStatus.APPROVED) {
            throw new RuntimeException("Loan must be approved before disbursement");
        }

        // Create disbursement record
        Disbursement disbursement = Disbursement.builder()
                .loan(loan)
                .amount(loan.getApprovedAmount().subtract(loan.getDownPayment()))
                .disbursementMethod(request.getDisbursementMethod())
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .bankCode(request.getBankCode())
                .status(Disbursement.DisbursementStatus.PENDING)
                .build();

        disbursementRepository.save(disbursement);

        // Simulate disbursement processing
        processDisbursement(disbursement);

        // Update loan status
        loan.setStatus(Loan.LoanStatus.DISBURSED);
        loan.setDisbursedAt(LocalDateTime.now());
        loanRepository.save(loan);

        kafkaTemplate.send("disbursement-events", "LOAN_DISBURSED:" + loanId);
    }

    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        return mapToResponse(loan);
    }

    public List<LoanResponse> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Helper methods
    private void validateLoanRequest(LoanApplicationRequest request, LoanProduct product) {
        if (request.getRequestedAmount().compareTo(product.getMinAmount()) < 0 ||
            request.getRequestedAmount().compareTo(product.getMaxAmount()) > 0) {
            throw new RuntimeException("Loan amount out of product range");
        }
        if (request.getTenor() < product.getMinTenor() || request.getTenor() > product.getMaxTenor()) {
            throw new RuntimeException("Tenor out of product range");
        }
    }

    private BigDecimal calculateMonthlyInstallment(BigDecimal principal, BigDecimal monthlyRate, int tenor) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenor), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal numerator = principal.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(
                        BigDecimal.ONE.add(monthlyRate).pow(tenor), 10, RoundingMode.HALF_UP
                )
        );
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private void createInitialApprovals(Loan loan) {
        for (Approval.ApprovalLevel level : Approval.ApprovalLevel.values()) {
            Approval approval = Approval.builder()
                    .loan(loan)
                    .approvalLevel(level)
                    .decision(Approval.ApprovalDecision.PENDING)
                    .build();
            approvalRepository.save(approval);
        }
    }

    private boolean isFullyApproved(Long loanId) {
        List<Approval> approvals = approvalRepository.findByLoanId(loanId);
        return approvals.stream()
                .allMatch(a -> a.getDecision() == Approval.ApprovalDecision.APPROVED);
    }

    private void processDisbursement(Disbursement disbursement) {
        // Simulate disbursement processing
        try {
            Thread.sleep(1000); // Simulate processing delay
            disbursement.setStatus(Disbursement.DisbursementStatus.COMPLETED);
            disbursement.setDisbursedAt(LocalDateTime.now());
            disbursementRepository.save(disbursement);
            log.info("Disbursement completed for loan: {}", disbursement.getLoan().getId());
        } catch (InterruptedException e) {
            disbursement.setStatus(Disbursement.DisbursementStatus.FAILED);
            disbursementRepository.save(disbursement);
            log.error("Disbursement failed", e);
        }
    }

    private LoanResponse mapToResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .customerId(loan.getCustomerId())
                .productId(loan.getProduct().getId())
                .productName(loan.getProduct().getProductName())
                .requestedAmount(loan.getRequestedAmount())
                .approvedAmount(loan.getApprovedAmount())
                .tenor(loan.getTenor())
                .downPayment(loan.getDownPayment())
                .interestRate(loan.getInterestRate())
                .monthlyInstallment(loan.getMonthlyInstallment())
                .totalPayment(loan.getTotalPayment())
                .purpose(loan.getPurpose())
                .status(loan.getStatus())
                .remarks(loan.getRemarks())
                .createdAt(loan.getCreatedAt())
                .approvedAt(loan.getApprovedAt())
                .disbursedAt(loan.getDisbursedAt())
                .build();
    }
}
