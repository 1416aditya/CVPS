



// package com.heg.cvps.controller;

// import java.io.IOException;
// import java.util.List;

// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;

// import com.heg.cvps.entity.CvpsEmployeeDetail;
// import com.heg.cvps.entity.CvpsRequest;
// import com.heg.cvps.entity.CvpsVehicleDocument;
// import com.heg.cvps.service.CvpsDocumentService;
// import com.heg.cvps.service.CvpsEmployeeDetailService;
// import com.heg.cvps.service.CvpsRequestService;

// import jakarta.validation.Valid;

// @CrossOrigin(origins = "*", allowedHeaders = "*") // 🆕 UNBLOCKS YOUR FRONTEND TEAMMATE FROM CORS RESTRICTIONS
// @RestController
// @RequestMapping("/api/v1/permissions")
// public class CvpsApiController {

//     private final CvpsRequestService service;
//     private final CvpsDocumentService documentService;
//     private final CvpsEmployeeDetailService employeeService;

//     public CvpsApiController(CvpsRequestService service, 
//                              CvpsDocumentService documentService, 
//                              CvpsEmployeeDetailService employeeService) {
//         this.service = service;
//         this.documentService = documentService;
//         this.employeeService = employeeService;
//     }

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

//     @PostMapping(value = "/{requestNo}/upload-all-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<String> uploadAllDocuments(
//             @PathVariable Long requestNo,
//             @RequestParam("documentType") String[] documentTypes,
//             @RequestParam("documentNo") String[] documentNos,
//             @RequestParam("validFrom") String[] validFroms,
//             @RequestParam(value = "validTill", required = false) String[] validTills,
//             @RequestParam("files") List<MultipartFile> files) throws IOException {
        
//         for (int i = 0; i < files.size(); i++) {
//             String tillDate = (validTills != null && i < validTills.length) ? validTills[i] : null;
//             documentService.uploadVehicleDocument(requestNo, documentTypes[i], documentNos[i], validFroms[i], tillDate, files.get(i));
//         }
//         return new ResponseEntity<>("All vehicle documents processed and saved to Oracle successfully.", HttpStatus.CREATED);
//     }

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

//     @PostMapping("/{requestNo}/add-personnel")
//     public ResponseEntity<CvpsEmployeeDetail> addPersonnel(
//             @PathVariable Long requestNo,
//             @Valid @RequestBody CvpsEmployeeDetail payload) {
        
//         CvpsEmployeeDetail registeredPerson = employeeService.registerPersonnel(requestNo, payload);
//         return new ResponseEntity<>(registeredPerson, HttpStatus.CREATED);
//     }

//     // =========================================================================
//     // REFACTORED PHASE 7 & 8: CLEAN WORKFLOW ACTION ENDPOINT WITH JSON BODY
//     // =========================================================================
//     @PostMapping("/{requestNo}/workflow-action")
//     public ResponseEntity<CvpsRequest> performWorkflowAction(
//             @PathVariable Long requestNo,
//             @RequestBody WorkflowActionRequest body) {
        
//         CvpsRequest updatedRequest = service.processWorkflowAction(
//                 requestNo, 
//                 body.getAction(), 
//                 body.getEmpNo(), 
//                 body.getRemarks()
//         );
//         return ResponseEntity.ok(updatedRequest);
//     }

//     // =========================================================================
//     // PHASE 9: SUMMARY REPORTING AND LOOKUP ENDPOINTS
//     // =========================================================================
    
//     /**
//      * 1. Get all requests filtered by workflow status queue
//      * Example: GET /api/v1/permissions/summary/filter?status=CREATED
//      */
//     @GetMapping("/summary/filter")
//     public ResponseEntity<List<CvpsRequest>> getSummaryByStatus(@RequestParam("status") String status) {
//         List<CvpsRequest> filteredList = service.getRequestsByStatus(status);
//         return ResponseEntity.ok(filteredList);
//     }

//     /**
//      * 2. Security Gate Terminal validation for physical entry check
//      * Example: GET /api/v1/permissions/summary/validate-gate/MP04AB1234
//      */
//     @GetMapping("/summary/validate-gate/{vehicleNo}")
//     public ResponseEntity<CvpsRequest> verifyGateEntryPass(@PathVariable String vehicleNo) {
//         CvpsRequest approvedPass = service.validateGatePass(vehicleNo);
//         return ResponseEntity.ok(approvedPass);
//     }
// }

// // =============================================================================
// // DATA TRANSFER OBJECT (DTO) FOR THE CLEAN BODY PAYLOAD
// // =============================================================================
// class WorkflowActionRequest {
//     private String action;
//     private String empNo;
//     private String remarks;

//     // Getters and Setters
//     public String getAction() { 
//         return action; 
//     }
//     public void setAction(String action) { 
//         this.action = action; 
//     }

//     public String getEmpNo() { 
//         return empNo; 
//     }
//     public void setEmpNo(String empNo) { 
//         this.empNo = empNo; 
//     }

//     public String getRemarks() { 
//         return remarks; 
//     }
//     public void setRemarks(String remarks) { 
//         this.remarks = remarks; 
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.heg.cvps.entity.CvpsEmployeeDetail;
import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.entity.CvpsVehicleDocument;
import com.heg.cvps.service.CvpsDocumentService;
import com.heg.cvps.service.CvpsEmployeeDetailService;
import com.heg.cvps.service.CvpsRequestService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*") // UNBLOCKS YOUR FRONTEND TEAMMATE FROM CORS RESTRICTIONS
@RestController
@RequestMapping("/api/v1/permissions")
public class CvpsApiController {

    private final CvpsRequestService service;
    private final CvpsDocumentService documentService;
    private final CvpsEmployeeDetailService employeeService;

    public CvpsApiController(CvpsRequestService service, 
                             CvpsDocumentService documentService, 
                             CvpsEmployeeDetailService employeeService) {
        this.service = service;
        this.documentService = documentService;
        this.employeeService = employeeService;
    }

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

    @PostMapping(value = "/{requestNo}/upload-all-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAllDocuments(
            @PathVariable Long requestNo,
            @RequestParam("documentType") String[] documentTypes,
            @RequestParam("documentNo") String[] documentNos,
            @RequestParam("validFrom") String[] validFroms,
            @RequestParam(value = "validTill", required = false) String[] validTills,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        
        for (int i = 0; i < files.size(); i++) {
            String tillDate = (validTills != null && i < validTills.length) ? validTills[i] : null;
            documentService.uploadVehicleDocument(requestNo, documentTypes[i], documentNos[i], validFroms[i], tillDate, files.get(i));
        }
        return new ResponseEntity<>("All vehicle documents processed and saved to Oracle successfully.", HttpStatus.CREATED);
    }

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

    @PostMapping("/{requestNo}/add-personnel")
    public ResponseEntity<CvpsEmployeeDetail> addPersonnel(
            @PathVariable Long requestNo,
            @Valid @RequestBody CvpsEmployeeDetail payload) {
        
        CvpsEmployeeDetail registeredPerson = employeeService.registerPersonnel(requestNo, payload);
        return new ResponseEntity<>(registeredPerson, HttpStatus.CREATED);
    }

    // =========================================================================
    // PHASE 7 & 8: WORKFLOW ACTION ENDPOINT WITH JSON BODY
    // =========================================================================
    @PostMapping("/{requestNo}/workflow-action")
    public ResponseEntity<CvpsRequest> performWorkflowAction(
            @PathVariable Long requestNo,
            @RequestBody WorkflowActionRequest body) {
        
        CvpsRequest updatedRequest = service.processWorkflowAction(
                requestNo, 
                body.getAction(), 
                body.getEmpNo(), 
                body.getRemarks()
        );
        return ResponseEntity.ok(updatedRequest);
    }

    // =========================================================================
    // PHASE 9: SUMMARY REPORTING AND LOOKUP ENDPOINTS
    // =========================================================================
    
    /**
     * 1. Get all requests filtered by workflow status queue
     * Example: GET /api/v1/permissions/summary/filter?status=CREATED
     */
    @GetMapping("/summary/filter")
    public ResponseEntity<List<CvpsRequest>> getSummaryByStatus(@RequestParam("status") String status) {
        List<CvpsRequest> filteredList = service.getRequestsByStatus(status);
        return ResponseEntity.ok(filteredList);
    }

    /**
     * 2. Security Gate Terminal validation for physical entry check
     * Example: GET /api/v1/permissions/summary/validate-gate/MP04AB1234
     */
    @GetMapping("/summary/validate-gate/{vehicleNo}")
    public ResponseEntity<CvpsRequest> verifyGateEntryPass(@PathVariable String vehicleNo) {
        CvpsRequest approvedPass = service.validateGatePass(vehicleNo);
        return ResponseEntity.ok(approvedPass);
    }

    // =========================================================================
    // MODIFICATION AND EDITING ENGINE ROUTE HANDLERS
    // =========================================================================

    /**
     * Updates text field metadata details for an active request form.
     * Enforces the modification validation check constraint before mutating database variables.
     * Example: PUT /api/v1/permissions/100001/modify
     */
    @PutMapping("/{requestNo}/modify")
    public ResponseEntity<CvpsRequest> modifyVehicleRequestDetails(
            @PathVariable Long requestNo,
            @Valid @RequestBody CvpsRequest modifiedPayload) {
        
        CvpsRequest updatedRecord = service.updateVehicleRequestDetails(requestNo, modifiedPayload);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Replaces or overwrites an individual uploaded vehicle document type file asset.
     * Example: POST /api/v1/permissions/100001/replace-document
     */
    @PostMapping(value = "/{requestNo}/replace-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> replaceSingleVehicleDocument(
            @PathVariable Long requestNo,
            @RequestParam("documentType") String documentType,
            @RequestParam("documentNo") String documentNo,
            @RequestParam("validFrom") String validFrom,
            @RequestParam(value = "validTill", required = false) String validTill,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        documentService.replaceVehicleDocument(requestNo, documentType, documentNo, validFrom, validTill, file);
        return ResponseEntity.ok("Vehicle document type " + documentType + " successfully updated and replaced in storage.");
    }
}

// =============================================================================
// DATA TRANSFER OBJECT (DTO) FOR THE CLEAN BODY PAYLOAD
// =============================================================================
class WorkflowActionRequest {
    private String action;
    private String empNo;
    private String remarks;

    // Getters and Setters
    public String getAction() { 
        return action; 
    }
    public void setAction(String action) { 
        this.action = action; 
    }

    public String getEmpNo() { 
        return empNo; 
    }
    public void setEmpNo(String empNo) { 
        this.empNo = empNo; 
    }

    public String getRemarks() { 
        return remarks; 
    }
    public void setRemarks(String remarks) { 
        this.remarks = remarks; 
    }
}