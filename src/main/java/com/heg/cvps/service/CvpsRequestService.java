




// package com.heg.cvps.service;

// import java.util.List;
// import java.util.NoSuchElementException;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.heg.cvps.entity.CvpsRequest;
// import com.heg.cvps.entity.CvpsRequestHistory;
// import com.heg.cvps.repository.CvpsRequestHistoryRepository;
// import com.heg.cvps.repository.CvpsRequestRepository;

// @Service
// public class CvpsRequestService {

//     private final CvpsRequestRepository repository;
//     private final CvpsRequestHistoryRepository historyRepository;

//     // Inject both repositories into the service constructor
//     public CvpsRequestService(CvpsRequestRepository repository, CvpsRequestHistoryRepository historyRepository) {
//         this.repository = repository;
//         this.historyRepository = historyRepository;
//     }

//     public CvpsRequest saveVehicleRequest(CvpsRequest request) {
//         return repository.save(request);
//     }

//     public CvpsRequest getRequestByVehicleNo(String vehicleNo) {
//         return repository.findByVehicleNo(vehicleNo)
//                 .orElseThrow(() -> new NoSuchElementException("No active request on file for vehicle number: " + vehicleNo));
//     }

//     public List<CvpsRequest> getAllRequests() {
//         return repository.findAll();
//     }

//     // Phase 7 & 8: WORKFLOW STATE PROCESSING ENGINE
//     @Transactional
//     public CvpsRequest processWorkflowAction(Long requestNo, String actionTaken, String empNo, String remarks) {
//         // 1. Fetch parent request from Oracle
//         CvpsRequest cvpsRequest = repository.findById(requestNo)
//                 .orElseThrow(() -> new NoSuchElementException("Request Number " + requestNo + " does not exist."));

//         // 2. Map actions to target status configurations from your PDF blueprint
//         String newStatus;
//         switch (actionTaken.toUpperCase()) {
//             case "CONFIRM":
//                 newStatus = "CONFIRMED";
//                 break;
//             case "VERIFY":
//                 newStatus = "VERIFIED";
//                 break;
//             case "APPROVE":
//                 newStatus = "APPROVED";
//                 break;
//             case "REJECT":
//                 newStatus = "REJECTED";
//                 break;
//             case "HOLD":
//                 newStatus = "HOLD";
//                 break;
//             default:
//                 throw new IllegalArgumentException("Invalid workflow action! Must be CONFIRM, VERIFY, APPROVE, REJECT, or HOLD.");
//         }

//         // 3. Update parent permission request state
//         cvpsRequest.setReqStatus(newStatus);
//         repository.save(cvpsRequest);

//         // 4. Create an IMMUTABLE audit history trail row (Never update or delete history)
//         CvpsRequestHistory history = new CvpsRequestHistory();
//         history.setRequest(cvpsRequest);
//         history.setActionTaken(newStatus);
//         history.setEmpNo(empNo);
//         history.setRemarks(remarks);
        
//         historyRepository.save(history);

//         return cvpsRequest;
//     }

//     // =========================================================================
//     // 🆕 PHASE 9: SUMMARY REPORTING AND LOOKUP LOGIC
//     // =========================================================================

//     /**
//      * Fetches all vehicle permission requests filtered by their current lifecycle state.
//      * Useful for building queue dashboards for different operational roles.
//      */
//     public List<CvpsRequest> getRequestsByStatus(String status) {
//         return repository.findByReqStatus(status.toUpperCase());
//     }

//     /**
//      * Validates if a vehicle has an active, fully cleared and 'APPROVED' gate pass.
//      * If the status is anything else, it throws an exception to reject gate terminal access.
//      */
//     public CvpsRequest validateGatePass(String vehicleNo) {
//         return repository.findByVehicleNoAndReqStatus(vehicleNo, "APPROVED")
//                 .orElseThrow(() -> new NoSuchElementException("Vehicle " + vehicleNo + " does not have an APPROVED gate pass on file. Access Denied."));
//     }
// }



package com.heg.cvps.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.entity.CvpsRequestHistory;
import com.heg.cvps.repository.CvpsRequestHistoryRepository;
import com.heg.cvps.repository.CvpsRequestRepository;

@Service
public class CvpsRequestService {

    private final CvpsRequestRepository repository;
    private final CvpsRequestHistoryRepository historyRepository;

    // Inject both repositories into the service constructor
    public CvpsRequestService(CvpsRequestRepository repository, CvpsRequestHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public CvpsRequest saveVehicleRequest(CvpsRequest request) {
        return repository.save(request);
    }

    public CvpsRequest getRequestByVehicleNo(String vehicleNo) {
        return repository.findByVehicleNo(vehicleNo)
                .orElseThrow(() -> new NoSuchElementException("No active request on file for vehicle number: " + vehicleNo));
    }

    public List<CvpsRequest> getAllRequests() {
        return repository.findAll();
    }

    // Phase 7 & 8: WORKFLOW STATE PROCESSING ENGINE
    @Transactional
    public CvpsRequest processWorkflowAction(Long requestNo, String actionTaken, String empNo, String remarks) {
        // 1. Fetch parent request from Oracle
        CvpsRequest cvpsRequest = repository.findById(requestNo)
                .orElseThrow(() -> new NoSuchElementException("Request Number " + requestNo + " does not exist."));

        // 2. Map actions to target status configurations from your PDF blueprint
        String newStatus;
        switch (actionTaken.toUpperCase()) {
            case "CONFIRM":
                newStatus = "CONFIRMED";
                break;
            case "VERIFY":
                newStatus = "VERIFIED";
                break;
            case "APPROVE":
                newStatus = "APPROVED";
                break;
            case "REJECT":
                newStatus = "REJECTED";
                break;
            case "HOLD":
                newStatus = "HOLD";
                break;
            default:
                throw new IllegalArgumentException("Invalid workflow action! Must be CONFIRM, VERIFY, APPROVE, REJECT, or HOLD.");
        }

        // 3. Update parent permission request state
        cvpsRequest.setReqStatus(newStatus);
        repository.save(cvpsRequest);

        // 4. Create an IMMUTABLE audit history trail row (Never update or delete history)
        CvpsRequestHistory history = new CvpsRequestHistory();
        history.setRequest(cvpsRequest);
        history.setActionTaken(newStatus);
        history.setEmpNo(empNo);
        history.setRemarks(remarks);
        
        historyRepository.save(history);

        return cvpsRequest;
    }

    // =========================================================================
    // PHASE 9: SUMMARY REPORTING AND LOOKUP LOGIC
    // =========================================================================

    /**
     * Fetches all vehicle permission requests filtered by their current lifecycle state.
     * Useful for building queue dashboards for different operational roles.
     */
    public List<CvpsRequest> getRequestsByStatus(String status) {
        return repository.findByReqStatus(status.toUpperCase());
    }

    /**
     * Validates if a vehicle has an active, fully cleared and 'APPROVED' gate pass.
     * If the status is anything else, it throws an exception to reject gate terminal access.
     */
    public CvpsRequest validateGatePass(String vehicleNo) {
        return repository.findByVehicleNoAndReqStatus(vehicleNo, "APPROVED")
                .orElseThrow(() -> new NoSuchElementException("Vehicle " + vehicleNo + " does not have an APPROVED gate pass on file. Access Denied."));
    }

    // =========================================================================
    // 🆕 MODIFICATION & RESUBMISSION ENGINE (Phase 3 Rule Integration)
    // =========================================================================
    
    /**
     * Updates an existing request's core text details if it is still editable.
     * Denies modifications if the request has already been processed past the draft stages.
     */
    @Transactional
    public CvpsRequest updateVehicleRequestDetails(Long requestNo, CvpsRequest updatedData) {
        // 1. Fetch existing request from Oracle
        CvpsRequest existingRequest = repository.findById(requestNo)
                .orElseThrow(() -> new NoSuchElementException("Request Number " + requestNo + " does not exist."));

        // 2. Enforce Blueprint Rule: Only modify if status is CREATED or HOLD
        String status = existingRequest.getReqStatus().toUpperCase();
        if (!status.equals("CREATED") && !status.equals("HOLD")) {
            throw new IllegalArgumentException("Modification Denied! Cannot edit a request that is currently in " + status + " status.");
        }

        // 3. Update editable fields safely
        existingRequest.setNatureOfJob(updatedData.getNatureOfJob());
        existingRequest.setVehicleNo(updatedData.getVehicleNo());
        existingRequest.setVehicleType(updatedData.getVehicleType());
        existingRequest.setPermissionFrom(updatedData.getPermissionFrom());
        existingRequest.setPermissionTo(updatedData.getPermissionTo());

        // 4. If it was on HOLD, automatically reset it to CREATED for re-evaluation queue routing
        if (status.equals("HOLD")) {
            existingRequest.setReqStatus("CREATED");
        }

        // 5. Commit changes back to Oracle database
        return repository.save(existingRequest);
    }
}