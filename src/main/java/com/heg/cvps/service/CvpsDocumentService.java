package com.heg.cvps.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.entity.CvpsVehicleDocument;
import com.heg.cvps.repository.CvpsRequestRepository;
import com.heg.cvps.repository.CvpsVehicleDocumentRepository;

@Service
public class CvpsDocumentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final CvpsVehicleDocumentRepository docRepository;
    private final CvpsRequestRepository requestRepository;

    public CvpsDocumentService(CvpsVehicleDocumentRepository docRepository, CvpsRequestRepository requestRepository) {
        this.docRepository = docRepository;
        this.requestRepository = requestRepository;
    }

    public CvpsVehicleDocument uploadVehicleDocument(Long requestNo, String docType, String docNo, 
                                                     String validFrom, String validTill, MultipartFile file) throws IOException {
        
        CvpsRequest cvpsRequest = requestRepository.findById(requestNo)
                .orElseThrow(() -> new IllegalArgumentException("Request ID " + requestNo + " not found."));

        // Create upload directory path if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a completely unique file name using systems timestamp
        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + uniqueFileName);
        
        // Write file out to disk partition
        Files.copy(file.getInputStream(), filePath);

        // Map data details to Oracle persistence model
        CvpsVehicleDocument vehicleDoc = new CvpsVehicleDocument();
        vehicleDoc.setRequest(cvpsRequest);
        vehicleDoc.setDocumentType(docType);
        vehicleDoc.setDocumentNo(docNo);
        vehicleDoc.setValidFrom(LocalDate.parse(validFrom));
        if (validTill != null && !validTill.isEmpty()) {
            vehicleDoc.setValidTill(LocalDate.parse(validTill));
        }
        vehicleDoc.setFilename(filePath.toString()); // Stores complete absolute path

        return docRepository.save(vehicleDoc);
    }

    public CvpsVehicleDocument getDocumentMetadata(Long documentId) {
        return docRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document ID record not found."));
    }
}