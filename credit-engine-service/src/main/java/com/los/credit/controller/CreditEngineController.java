package com.los.credit.controller;

import com.los.credit.dto.CreditCheckRequest;
import com.los.credit.dto.CreditCheckResponse;
import com.los.credit.service.CreditEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/credit")
@RequiredArgsConstructor
public class CreditEngineController {

    private final CreditEngineService creditEngineService;

    @PostMapping("/check")
    public ResponseEntity<CreditCheckResponse> performCreditCheck(@RequestBody CreditCheckRequest request) {
        CreditCheckResponse response = creditEngineService.performCreditCheck(request);
        return ResponseEntity.ok(response);
    }
}
