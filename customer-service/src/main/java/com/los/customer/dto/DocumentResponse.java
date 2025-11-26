package com.los.customer.dto;

import com.los.customer.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {
    private Long id;
    private Long customerId;
    private Document.DocumentType documentType;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String ocrData;
    private Document.DocumentStatus status;
    private LocalDateTime uploadedAt;
}
