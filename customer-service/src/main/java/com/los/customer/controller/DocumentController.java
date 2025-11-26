package com.los.customer.controller;

import com.los.customer.dto.DocumentResponse;
import com.los.customer.entity.Document;
import com.los.customer.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/customers/{customerId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long customerId,
            @RequestParam("documentType") Document.DocumentType documentType,
            @RequestParam("file") MultipartFile file) {
        DocumentResponse response = documentService.uploadDocument(customerId, documentType, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getCustomerDocuments(@PathVariable Long customerId) {
        List<DocumentResponse> response = documentService.getCustomerDocuments(customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{documentId}/verify")
    public ResponseEntity<Void> verifyDocument(@PathVariable Long customerId, @PathVariable Long documentId) {
        documentService.verifyDocument(documentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{documentId}/reject")
    public ResponseEntity<Void> rejectDocument(@PathVariable Long customerId, @PathVariable Long documentId) {
        documentService.rejectDocument(documentId);
        return ResponseEntity.ok().build();
    }
}
