package com.los.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> customerFallback() {
        return createFallbackResponse("Customer Service");
    }

    @GetMapping("/loans")
    public ResponseEntity<Map<String, Object>> loanFallback() {
        return createFallbackResponse("Loan Service");
    }

    @GetMapping("/credit")
    public ResponseEntity<Map<String, Object>> creditFallback() {
        return createFallbackResponse("Credit Engine Service");
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String serviceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", serviceName + " is currently unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
