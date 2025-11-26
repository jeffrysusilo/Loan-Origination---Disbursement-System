package com.los.customer.service;

import com.los.customer.dto.DocumentResponse;
import com.los.customer.entity.Customer;
import com.los.customer.entity.Document;
import com.los.customer.exception.CustomerNotFoundException;
import com.los.customer.mapper.DocumentMapper;
import com.los.customer.repository.CustomerRepository;
import com.los.customer.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final DocumentMapper documentMapper;
    private final OcrService ocrService;

    private static final String UPLOAD_DIR = "uploads/documents/";

    @Transactional
    public DocumentResponse uploadDocument(Long customerId, Document.DocumentType documentType, MultipartFile file) {
        log.info("Uploading document for customer ID: {}, type: {}", customerId, documentType);

        // Verify customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Perform OCR if document is KTP
            String ocrData = null;
            if (documentType == Document.DocumentType.KTP) {
                ocrData = ocrService.extractKtpData(filePath.toString());
            }

            // Save document metadata
            Document document = Document.builder()
                    .customer(customer)
                    .documentType(documentType)
                    .fileName(originalFilename)
                    .filePath(filePath.toString())
                    .mimeType(file.getContentType())
                    .fileSize(file.getSize())
                    .ocrData(ocrData)
                    .status(Document.DocumentStatus.UPLOADED)
                    .build();

            Document savedDocument = documentRepository.save(document);

            log.info("Document uploaded successfully with ID: {}", savedDocument.getId());
            return documentMapper.toResponse(savedDocument);

        } catch (IOException e) {
            log.error("Error uploading document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload document", e);
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getCustomerDocuments(Long customerId) {
        log.info("Fetching documents for customer ID: {}", customerId);

        // Verify customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        return documentRepository.findByCustomerId(customerId).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void verifyDocument(Long documentId) {
        log.info("Verifying document with ID: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        document.setStatus(Document.DocumentStatus.VERIFIED);
        documentRepository.save(document);

        log.info("Document verified successfully with ID: {}", documentId);
    }

    @Transactional
    public void rejectDocument(Long documentId) {
        log.info("Rejecting document with ID: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        document.setStatus(Document.DocumentStatus.REJECTED);
        documentRepository.save(document);

        log.info("Document rejected successfully with ID: {}", documentId);
    }
}
