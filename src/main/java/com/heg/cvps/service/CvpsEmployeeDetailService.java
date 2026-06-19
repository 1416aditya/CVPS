package com.heg.cvps.service;

import org.springframework.stereotype.Service;

import com.heg.cvps.entity.CvpsEmployeeDetail;
import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.repository.CvpsEmployeeDetailRepository;
import com.heg.cvps.repository.CvpsRequestRepository;

@Service
public class CvpsEmployeeDetailService {

    private final CvpsEmployeeDetailRepository employeeRepository;
    private final CvpsRequestRepository requestRepository;

    public CvpsEmployeeDetailService(CvpsEmployeeDetailRepository employeeRepository, CvpsRequestRepository requestRepository) {
        this.employeeRepository = employeeRepository;
        this.requestRepository = requestRepository;
    }

    public CvpsEmployeeDetail registerPersonnel(Long requestNo, CvpsEmployeeDetail detail) {
        // 1. Verify the parent Request number exists in Oracle DB
        CvpsRequest request = requestRepository.findById(requestNo)
                .orElseThrow(() -> new IllegalArgumentException("Request Number " + requestNo + " does not exist."));

        // 2. Enforce strict PDF business validations based on EMP_TYPE
        if ("REGISTERED".equalsIgnoreCase(detail.getEmpType())) {
            if (detail.getEmpNo() == null) {
                throw new IllegalArgumentException("Employee Number (empNo) is mandatory for REGISTERED personnel.");
            }
        } else if ("UNREGISTERED".equalsIgnoreCase(detail.getEmpType())) {
            if (detail.getName() == null || detail.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Name is mandatory for UNREGISTERED personnel.");
            }
            if (detail.getAadharNo() == null || detail.getAadharNo().trim().isEmpty()) {
                throw new IllegalArgumentException("Aadhaar Number (aadharNo) is mandatory for UNREGISTERED personnel.");
            }
            detail.setEmpNo(null); // Explicitly clean data integrity
        } else {
            throw new IllegalArgumentException("Invalid Employee Type! Must be REGISTERED or UNREGISTERED.");
        }

        // 3. Bind relationship
        detail.setRequest(request);

        // 4. Save to table
        return employeeRepository.save(detail);
    }
}