





package com.heg.cvps.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // =========================================================================
    // 🆕 DOCUMENT MODIFICATION & OVERWRITE LOGIC
    // =========================================================================
    
    /**
     * Overwrites/replaces an existing vehicle document file if it already exists for this request.
     * Deletes the old physical PDF from the storage partition before writing the new one.
     */
    @Transactional
    public void replaceVehicleDocument(Long requestNo, String documentType, String documentNo, 
                                       String validFrom, String validTill, MultipartFile file) throws IOException {
        
        // 1. Check if a document of this type already exists under this request
        Optional<CvpsVehicleDocument> existingDocOpt = 
                docRepository.findByRequest_RequestNoAndDocumentType(requestNo, documentType);

        if (existingDocOpt.isPresent()) {
            CvpsVehicleDocument oldDoc = existingDocOpt.get();
            
            // 2. Safely delete the old physical file from disk to prevent storage leakage
            try {
                Path oldFilePath = Paths.get(oldDoc.getFilename());
                Files.deleteIfExists(oldFilePath);
            } catch (Exception e) {
                System.err.println("Warning: Could not delete physical file from disk: " + oldDoc.getFilename());
            }

            // 3. Create upload directory if missing
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 4. Stream the new physical file asset to disk
            String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetPath = Paths.get(uploadDir + uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 5. Update the existing entity row with new document metadata details
            oldDoc.setDocumentNo(documentNo);
            oldDoc.setValidFrom(LocalDate.parse(validFrom));
            if (validTill != null && !validTill.isEmpty()) {
                oldDoc.setValidTill(LocalDate.parse(validTill));
            } else {
                oldDoc.setValidTill(null);
            }
            oldDoc.setFilename(targetPath.toString());

            docRepository.save(oldDoc);
        } else {
            // 6. Fallback: If document type wasn't uploaded before, handle it as a standard new upload
            uploadVehicleDocument(requestNo, documentType, documentNo, validFrom, validTill, file);
        }
    }
}