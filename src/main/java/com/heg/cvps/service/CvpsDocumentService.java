



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

import com.heg.cvps.entity.CvpsEmployeeDocument;
import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.entity.CvpsVehicleDocument; // 🆕 Added Import
import com.heg.cvps.repository.CvpsEmployeeDocumentRepository;
import com.heg.cvps.repository.CvpsRequestRepository;
import com.heg.cvps.repository.CvpsVehicleDocumentRepository; // 🆕 Added Import

@Service
public class CvpsDocumentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final CvpsVehicleDocumentRepository docRepository;
    private final CvpsRequestRepository requestRepository;
    private final CvpsEmployeeDocumentRepository empDocRepository; // 🆕 Injected Repository

    // Updated constructor to cleanly inject the new employee document repository dependency
    public CvpsDocumentService(CvpsVehicleDocumentRepository docRepository, 
                               CvpsRequestRepository requestRepository,
                               CvpsEmployeeDocumentRepository empDocRepository) {
        this.docRepository = docRepository;
        this.requestRepository = requestRepository;
        this.empDocRepository = empDocRepository;
    }

    public CvpsVehicleDocument uploadVehicleDocument(Long requestNo, String docType, String docNo, 
                                                     String validFrom, String validTill, MultipartFile file) throws IOException {
        
        CvpsRequest cvpsRequest = requestRepository.findById(requestNo)
                .orElseThrow(() -> new IllegalArgumentException("Request ID " + requestNo + " not found."));

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + uniqueFileName);
        
        Files.copy(file.getInputStream(), filePath);

        CvpsVehicleDocument vehicleDoc = new CvpsVehicleDocument();
        vehicleDoc.setRequest(cvpsRequest);
        vehicleDoc.setDocumentType(docType);
        vehicleDoc.setDocumentNo(docNo);
        vehicleDoc.setValidFrom(LocalDate.parse(validFrom));
        if (validTill != null && !validTill.isEmpty()) {
            vehicleDoc.setValidTill(LocalDate.parse(validTill));
        }
        vehicleDoc.setFilename(filePath.toString());

        return docRepository.save(vehicleDoc);
    }

    public CvpsVehicleDocument getDocumentMetadata(Long documentId) {
        return docRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document ID record not found."));
    }

    // =========================================================================
    // 🆕 NEW METHOD: PROCESSING ENGINE FOR DRIVER/PERSONNEL FILE STORAGE
    // =========================================================================
    /**
     * Stores driver/crew multi-part documents locally and persists metadata to Oracle.
     */
    public CvpsEmployeeDocument uploadEmployeeDocument(Long empId, String docType, String docNo, 
                                                       String validFrom, String validTill, MultipartFile file) throws IOException {
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
        CvpsEmployeeDocument employeeDoc = new CvpsEmployeeDocument();
        employeeDoc.setEmpId(empId);
        employeeDoc.setDocumentType(docType);
        employeeDoc.setDocumentNo(docNo);
        
        if (validFrom != null && !validFrom.isEmpty()) {
            employeeDoc.setValidFrom(LocalDate.parse(validFrom));
        }
        if (validTill != null && !validTill.isEmpty()) {
            employeeDoc.setValidTill(LocalDate.parse(validTill));
        }
        employeeDoc.setFilename(filePath.toString());

        return empDocRepository.save(employeeDoc);
    }

    // =========================================================================
    // DOCUMENT MODIFICATION & OVERWRITE LOGIC
    // =========================================================================
    @Transactional
    public void replaceVehicleDocument(Long requestNo, String documentType, String documentNo, 
                                       String validFrom, String validTill, MultipartFile file) throws IOException {
        
        Optional<CvpsVehicleDocument> existingDocOpt = 
                docRepository.findByRequest_RequestNoAndDocumentType(requestNo, documentType);

        if (existingDocOpt.isPresent()) {
            CvpsVehicleDocument oldDoc = existingDocOpt.get();
            
            try {
                Path oldFilePath = Paths.get(oldDoc.getFilename());
                Files.deleteIfExists(oldFilePath);
            } catch (Exception e) {
                System.err.println("Warning: Could not delete physical file from disk: " + oldDoc.getFilename());
            }

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetPath = Paths.get(uploadDir + uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

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
            uploadVehicleDocument(requestNo, documentType, documentNo, validFrom, validTill, file);
        }
    }
}