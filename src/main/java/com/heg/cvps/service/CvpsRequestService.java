









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

    @Transactional
    public CvpsRequest processWorkflowAction(Long requestNo, String actionTaken, String empNo, String remarks) {
        CvpsRequest cvpsRequest = repository.findById(requestNo)
                .orElseThrow(() -> new NoSuchElementException("Request Number " + requestNo + " does not exist."));

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
                // ── 🟢 UPDATED: Remapped target state to MODIFY when sent back ──
                newStatus = "MODIFY";
                break;
            default:
                throw new IllegalArgumentException("Invalid workflow action! Must be CONFIRM, VERIFY, APPROVE, REJECT, or HOLD.");
        }

        cvpsRequest.setReqStatus(newStatus);
        repository.save(cvpsRequest);

        CvpsRequestHistory history = new CvpsRequestHistory();
        history.setRequest(cvpsRequest);
        history.setActionTaken(newStatus);
        history.setEmpNo(empNo);
        history.setRemarks(remarks);
        
        historyRepository.save(history);

        return cvpsRequest;
    }

    public List<CvpsRequest> getRequestsByStatus(String status) {
        return repository.findByReqStatus(status.toUpperCase());
    }

    public CvpsRequest validateGatePass(String vehicleNo) {
        return repository.findByVehicleNoAndReqStatus(vehicleNo, "APPROVED")
                .orElseThrow(() -> new NoSuchElementException("Vehicle " + vehicleNo + " does not have an APPROVED gate pass on file. Access Denied."));
    }

    @Transactional
    public CvpsRequest updateVehicleRequestDetails(Long requestNo, CvpsRequest updatedData) {
        CvpsRequest existingRequest = repository.findById(requestNo)
                .orElseThrow(() -> new NoSuchElementException("Request Number " + requestNo + " does not exist."));

        String status = existingRequest.getReqStatus().toUpperCase();
        
        // ── 🟢 UPDATED: Safe Whitelist including new SUBMITTED and MODIFY status parameters ──
        if (!status.equals("SUBMITTED") && !status.equals("MODIFY") && !status.equals("CREATED") && !status.equals("HOLD") && !status.equals("SAVED") && !status.equals("DRAFT")) {
            throw new IllegalArgumentException("Modification Denied! Cannot edit a request that is currently in " + status + " status.");
        }

        existingRequest.setNatureOfJob(updatedData.getNatureOfJob());
        existingRequest.setVehicleNo(updatedData.getVehicleNo());
        existingRequest.setVehicleType(updatedData.getVehicleType());
        existingRequest.setPermissionFrom(updatedData.getPermissionFrom());
        existingRequest.setPermissionTo(updatedData.getPermissionTo());

        // ── 🟢 UPDATED: Direct status translation tracking routing to SUBMITTED ──
        if (updatedData.getReqStatus() != null && (updatedData.getReqStatus().toUpperCase().equals("SUBMITTED") || updatedData.getReqStatus().toUpperCase().equals("CREATED"))) {
            existingRequest.setReqStatus("SUBMITTED");
        } else if (status.equals("MODIFY") || status.equals("HOLD")) {
            existingRequest.setReqStatus("SUBMITTED");
        } else {
            existingRequest.setReqStatus(updatedData.getReqStatus() != null ? updatedData.getReqStatus().toUpperCase() : "SAVED");
        }

        return repository.save(existingRequest);
    }
}