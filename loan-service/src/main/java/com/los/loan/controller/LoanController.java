package com.los.loan.controller;

import com.los.loan.dto.*;
import com.los.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        LoanResponse response = loanService.applyForLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id) {
        LoanResponse response = loanService.getLoanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<LoanResponse> getLoanStatus(@PathVariable Long id) {
        LoanResponse response = loanService.getLoanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanResponse>> getLoansByCustomer(@PathVariable Long customerId) {
        List<LoanResponse> response = loanService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        List<LoanResponse> response = loanService.getAllLoans();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approveLoan(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalRequest request) {
        LoanResponse response = loanService.approveLoan(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/disburse")
    public ResponseEntity<Void> disburseLoan(
            @PathVariable Long id,
            @Valid @RequestBody DisbursementRequest request) {
        loanService.disburseLoan(id, request);
        return ResponseEntity.ok().build();
    }
}
