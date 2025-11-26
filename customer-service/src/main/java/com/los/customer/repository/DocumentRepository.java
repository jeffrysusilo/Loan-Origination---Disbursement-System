package com.los.customer.repository;

import com.los.customer.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCustomerId(Long customerId);
    List<Document> findByCustomerIdAndDocumentType(Long customerId, Document.DocumentType documentType);
}
