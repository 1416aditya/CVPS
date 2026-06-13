




// package com.heg.cvps.controller;

// import java.io.IOException;
// import java.util.List;

// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;

// import com.heg.cvps.entity.CvpsRequest;
// import com.heg.cvps.entity.CvpsVehicleDocument;
// import com.heg.cvps.service.CvpsDocumentService;
// import com.heg.cvps.service.CvpsRequestService;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/v1/permissions")
// public class CvpsApiController {

//     private final CvpsRequestService service;
//     private final CvpsDocumentService documentService;

//     // Unified constructor injecting both your original request service and the document service
//     public CvpsApiController(CvpsRequestService service, CvpsDocumentService documentService) {
//         this.service = service;
//         this.documentService = documentService;
//     }

//     // =========================================================================
//     // 🛡️ UNTOUCHED ORIGINAL WORKING ENDPOINTS
//     // =========================================================================

//     @PostMapping
//     public ResponseEntity<CvpsRequest> submitVehiclePermissionRequest(@Valid @RequestBody CvpsRequest incomingPayload) {
//         CvpsRequest savedRecord = service.saveVehicleRequest(incomingPayload);
//         return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
//     }

//     @GetMapping("/{vehicleNo}")
//     public ResponseEntity<CvpsRequest> inspectVehiclePermissionStatus(@PathVariable String vehicleNo) {
//         CvpsRequest activeRecord = service.getRequestByVehicleNo(vehicleNo);
//         return ResponseEntity.ok(activeRecord);
//     }

//     @GetMapping
//     public ResponseEntity<List<CvpsRequest>> getAllPermissionRequests() {
//         List<CvpsRequest> allRecords = service.getAllRequests();
//         return ResponseEntity.ok(allRecords);
//     }

//     // =========================================================================
//     // 📂 NEW DOCUMENT UPLOAD & DOWNLOAD MANAGEMENT ENDPOINTS
//     // =========================================================================

//     /**
//      * Phase 4: Upload a physical PDF document against a specific vehicle permission request.
//      * Consumes: multipart/form-data
//      */
//     @PostMapping(value = "/{requestNo}/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<CvpsVehicleDocument> uploadDocument(
//             @PathVariable Long requestNo,
//             @RequestParam("documentType") String documentType,
//             @RequestParam("documentNo") String documentNo,
//             @RequestParam("validFrom") String validFrom,
//             @RequestParam(value = "validTill", required = false) String validTill,
//             @RequestParam("file") MultipartFile file) throws IOException {
        
//         CvpsVehicleDocument savedDoc = documentService.uploadVehicleDocument(
//                 requestNo, documentType, documentNo, validFrom, validTill, file);
//         return new ResponseEntity<>(savedDoc, HttpStatus.CREATED);
//     }

//     /**
//      * Phase 4: Download an uploaded document using a clean, unified URL layout.
//      * Accessible directly inside Postman via Header and Google Chrome via active HTTP Session tracking.
//      * Produces: application/pdf
//      */
//     @GetMapping("/documents/{documentId}/download")
//     public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) throws IOException {
//         CvpsVehicleDocument metadata = documentService.getDocumentMetadata(documentId);
//         java.nio.file.Path filePath = java.nio.file.Paths.get(metadata.getFilename());
//         Resource resource = new UrlResource(filePath.toUri());

//         if (!resource.exists() || !resource.isReadable()) {
//             throw new RuntimeException("The requested physical PDF file could not be read or found on the server filesystem storage layer.");
//         }

//         return ResponseEntity.ok()
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                 .body(resource);
//     }
// }



package com.heg.cvps.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.entity.CvpsVehicleDocument;
import com.heg.cvps.service.CvpsDocumentService;
import com.heg.cvps.service.CvpsRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/permissions")
public class CvpsApiController {

    private final CvpsRequestService service;
    private final CvpsDocumentService documentService;

    // Unified constructor injecting both your original request service and the document service
    public CvpsApiController(CvpsRequestService service, CvpsDocumentService documentService) {
        this.service = service;
        this.documentService = documentService;
    }

    // =========================================================================
    // 🛡️ UNTOUCHED ORIGINAL WORKING ENDPOINTS
    // =========================================================================

    @PostMapping
    public ResponseEntity<CvpsRequest> submitVehiclePermissionRequest(@Valid @RequestBody CvpsRequest incomingPayload) {
        CvpsRequest savedRecord = service.saveVehicleRequest(incomingPayload);
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    @GetMapping("/{vehicleNo}")
    public ResponseEntity<CvpsRequest> inspectVehiclePermissionStatus(@PathVariable String vehicleNo) {
        CvpsRequest activeRecord = service.getRequestByVehicleNo(vehicleNo);
        return ResponseEntity.ok(activeRecord);
    }

    @GetMapping
    public ResponseEntity<List<CvpsRequest>> getAllPermissionRequests() {
        List<CvpsRequest> allRecords = service.getAllRequests();
        return ResponseEntity.ok(allRecords);
    }

    // =========================================================================
    // 📂 MULTIPLE DOCUMENT BULK UPLOAD & DOWNLOAD MANAGEMENT ENDPOINTS
    // =========================================================================

    /**
     * Phase 4: Upload all 4 required vehicle documents (RC, INSURANCE, PUC, FITNESS)
     * in ONE single POST request execution using parallel form-data arrays.
     * Consumes: multipart/form-data
     */
    @PostMapping(value = "/{requestNo}/upload-all-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAllDocuments(
            @PathVariable Long requestNo,
            @RequestParam("documentType") String[] documentTypes,
            @RequestParam("documentNo") String[] documentNos,
            @RequestParam("validFrom") String[] validFroms,
            @RequestParam(value = "validTill", required = false) String[] validTills,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        
        // Loop through the arrays step-by-step and save each document using your service logic
        for (int i = 0; i < files.size(); i++) {
            String tillDate = (validTills != null && i < validTills.length) ? validTills[i] : null;
            
            documentService.uploadVehicleDocument(
                    requestNo, 
                    documentTypes[i], 
                    documentNos[i], 
                    validFroms[i], 
                    tillDate, 
                    files.get(i)
            );
        }
        
        return new ResponseEntity<>("All vehicle documents processed and saved to Oracle successfully.", HttpStatus.CREATED);
    }

    /**
     * Phase 4: Download an uploaded document using a clean, unified URL layout.
     * Accessible directly inside Postman via Header validation tracking.
     * Produces: application/pdf
     */
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) throws IOException {
        CvpsVehicleDocument metadata = documentService.getDocumentMetadata(documentId);
        java.nio.file.Path filePath = java.nio.file.Paths.get(metadata.getFilename());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("The requested physical PDF file could not be read or found on the server filesystem storage layer.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}