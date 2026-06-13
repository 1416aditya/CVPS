// package com.heg.cvps.service;

// import java.util.NoSuchElementException;

// import org.springframework.stereotype.Service;

// import com.heg.cvps.entity.CvpsRequest;
// import com.heg.cvps.repository.CvpsRequestRepository;

// @Service
// public class CvpsRequestService {

//     private final CvpsRequestRepository repository;

//     public CvpsRequestService(CvpsRequestRepository repository) {
//         this.repository = repository;
//     }

//     // Direct save to Oracle 10g Database
//     public CvpsRequest saveVehicleRequest(CvpsRequest request) {
//         return repository.save(request);
//     }

//     // Direct lookup from Oracle 10g Database
//     public CvpsRequest getRequestByVehicleNo(String vehicleNo) {
//         return repository.findByVehicleNo(vehicleNo)
//                 .orElseThrow(() -> new NoSuchElementException("No active permission record on file for vehicle tag: " + vehicleNo));
//     }
// }


package com.heg.cvps.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.repository.CvpsRequestRepository;

@Service
public class CvpsRequestService {

    private final CvpsRequestRepository repository;

    public CvpsRequestService(CvpsRequestRepository repository) {
        this.repository = repository;
    }

    public CvpsRequest saveVehicleRequest(CvpsRequest request) {
        return repository.save(request);
    }

    public CvpsRequest getRequestByVehicleNo(String vehicleNo) {
        return repository.findByVehicleNo(vehicleNo)
                .orElseThrow(() -> new NoSuchElementException("No active permission record on file for vehicle tag: " + vehicleNo));
    }

    // 🆕 ADD THIS METHOD TO FETCH ALL DATA
    public List<CvpsRequest> getAllRequests() {
        return repository.findAll();
    }
}