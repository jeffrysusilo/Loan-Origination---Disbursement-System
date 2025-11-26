package com.los.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class OcrService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    /**
     * Simulate OCR extraction from KTP document
     * In production, this would integrate with actual OCR services like:
     * - Google Cloud Vision API
     * - AWS Textract
     * - Azure Computer Vision
     * - Tesseract OCR
     */
    public String extractKtpData(String filePath) {
        log.info("Performing OCR on KTP document: {}", filePath);

        try {
            // Simulate OCR processing delay
            Thread.sleep(500);

            // Simulate extracted data
            Map<String, String> extractedData = new HashMap<>();
            extractedData.put("nik", generateSimulatedNik());
            extractedData.put("nama", "JOHN DOE");
            extractedData.put("tempatLahir", "JAKARTA");
            extractedData.put("tanggalLahir", "15-01-1990");
            extractedData.put("jenisKelamin", "LAKI-LAKI");
            extractedData.put("alamat", "JL. SUDIRMAN NO. 123");
            extractedData.put("rtRw", "001/002");
            extractedData.put("kelDesa", "KEBAYORAN BARU");
            extractedData.put("kecamatan", "KEBAYORAN BARU");
            extractedData.put("agama", "ISLAM");
            extractedData.put("statusPerkawinan", "BELUM KAWIN");
            extractedData.put("pekerjaan", "KARYAWAN SWASTA");
            extractedData.put("kewarganegaraan", "WNI");
            extractedData.put("berlakuHingga", "SEUMUR HIDUP");

            String ocrResult = objectMapper.writeValueAsString(extractedData);
            log.info("OCR extraction completed successfully");

            return ocrResult;

        } catch (Exception e) {
            log.error("Error during OCR extraction: {}", e.getMessage(), e);
            return "{}"; // Return empty JSON on error
        }
    }

    private String generateSimulatedNik() {
        // Generate random 16-digit NIK (for simulation only)
        StringBuilder nik = new StringBuilder("3174"); // Jakarta code
        for (int i = 0; i < 12; i++) {
            nik.append(random.nextInt(10));
        }
        return nik.toString();
    }
}
