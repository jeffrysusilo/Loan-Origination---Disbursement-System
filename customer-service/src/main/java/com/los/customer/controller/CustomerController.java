package com.los.customer.controller;

import com.los.customer.dto.CustomerRequest;
import com.los.customer.dto.CustomerResponse;
import com.los.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nik/{nik}")
    public ResponseEntity<CustomerResponse> getCustomerByNik(@PathVariable String nik) {
        CustomerResponse response = customerService.getCustomerByNik(nik);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Void> verifyCustomer(@PathVariable Long id) {
        customerService.verifyCustomer(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/blacklist")
    public ResponseEntity<Void> blacklistCustomer(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Manual blacklist") String reason) {
        customerService.blacklistCustomer(id, reason);
        return ResponseEntity.ok().build();
    }
}
