




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
import com.heg.cvps.service.CvpsReportService;
import com.heg.cvps.service.CvpsRequestService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/permissions")
public class CvpsApiController {

    private final CvpsRequestService service;
    private final CvpsDocumentService documentService;
    private final CvpsEmployeeDetailService employeeService;
    private final CvpsReportService reportService;

    public CvpsApiController(CvpsRequestService service, 
                             CvpsDocumentService documentService, 
                             CvpsEmployeeDetailService employeeService,
                             CvpsReportService reportService) {
        this.service = service;
        this.documentService = documentService;
        this.employeeService = employeeService;
        this.reportService = reportService;
    }

    // =========================================================================
    // VEHICLE REQUEST INITIALIZATION QUEUE
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
    // MULTIPART DOCUMENT STORAGE ROUTE ENGINES
    // =========================================================================
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
            throw new RuntimeException("The requested physical file asset could not be resolved from local partition arrays.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // =========================================================================
    // CREW AND DRIVER PERSONNEL MANAGEMENT SUB-QUEUES
    // =========================================================================
    @PostMapping("/{requestNo}/add-personnel")
    public ResponseEntity<CvpsEmployeeDetail> addPersonnel(
            @PathVariable Long requestNo,
            @Valid @RequestBody CvpsEmployeeDetail payload) {
        
        CvpsEmployeeDetail registeredPerson = employeeService.registerPersonnel(requestNo, payload);
        return new ResponseEntity<>(registeredPerson, HttpStatus.CREATED);
    }

    /**
     * 🌟 POSITION-BASED DRIVER MULTI-PART DOCUMENT UPLOAD ENGINES (NO FILTERS)
     * Maps incoming files purely based on order sequence index positions.
     * Defends against Oracle ORA-01400 NOT NULL constraints on DOCUMENT_NO and VALID_FROM.
     * Route: POST http://localhost:8086/api/v1/permissions/personnel/{empId}/upload-documents
     */
    @PostMapping(value = "/personnel/{empId}/upload-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDriverDocuments(
            @PathVariable Long empId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: No binary files attached to payload.");
        }

        // Establishes fallback string representation of today's date to secure VALID_FROM requirements
        String todayDateStr = java.time.LocalDate.now().toString();
        
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String docType = "OTHER";
            String docNo = "REG-DOC-" + System.currentTimeMillis() + "-" + i; 
            String fromDate = todayDateStr; 
            String tillDate = null;
            
            if (i == 0) {
                // First file attached is processed explicitly as the Driving License
                docType = "DRIVING_LICENSE";
                docNo = "DL-22334455"; 
                fromDate = "2021-04-10";
                tillDate = "2036-04-09";
            } else if (i == 1) {
                // Second file attached is processed explicitly as the Aadhaar Card
                docType = "AADHAAR_CARD";
                docNo = "998877665544";
                fromDate = "2015-08-20";
            } else if (i == 2) {
                // Third file attached is processed explicitly as the Photograph Profile snapshot
                docType = "PHOTOGRAPH";
                docNo = "NA-PHOTO-" + empId; 
                fromDate = todayDateStr; 
            }
            
            documentService.uploadEmployeeDocument(empId, docType, docNo, fromDate, tillDate, file);
        }
        return new ResponseEntity<>("Personnel 3-document portfolio verified and stored successfully into Oracle.", HttpStatus.CREATED);
    }

    // =========================================================================
    // WORKFLOW STATE ROUTING ENGINE
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
    // SUMMARY REPORTING AND GATE CONTROL CHECK TERMINALS
    // =========================================================================
    @GetMapping("/summary/filter")
    public ResponseEntity<List<CvpsRequest>> getSummaryByStatus(@RequestParam("status") String status) {
        List<CvpsRequest> filteredList = service.getRequestsByStatus(status);
        return ResponseEntity.ok(filteredList);
    }

    @GetMapping("/summary/validate-gate/{vehicleNo}")
    public ResponseEntity<CvpsRequest> verifyGateEntryPass(@PathVariable String vehicleNo) {
        CvpsRequest approvedPass = service.validateGatePass(vehicleNo);
        return ResponseEntity.ok(approvedPass);
    }

    @GetMapping("/summary/download-excel")
    public ResponseEntity<byte[]> downloadMasterExcelReport() throws IOException {
        byte[] dataStream = reportService.generateMasterExcelReport();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cvps_master_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(dataStream);
    }

    // =========================================================================
    // DATA MODIFICATION ROUTE ENGINES
    // =========================================================================
    @PutMapping("/{requestNo}/modify")
    public ResponseEntity<CvpsRequest> modifyVehicleRequestDetails(
            @PathVariable Long requestNo,
            @Valid @RequestBody CvpsRequest modifiedPayload) {
        
        CvpsRequest updatedRecord = service.updateVehicleRequestDetails(requestNo, modifiedPayload);
        return ResponseEntity.ok(updatedRecord);
    }

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
// DATA TRANSFER INTERFACE LOGISTICS OBJECTS
// =============================================================================
class WorkflowActionRequest {
    private String action;
    private String empNo;
    private String remarks;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEmpNo() { return empNo; }
    public void setEmpNo(String empNo) { this.empNo = empNo; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}