// package com.heg.cvps.repository;

// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.heg.cvps.entity.CvpsRequest;

// @Repository
// public interface CvpsRequestRepository extends JpaRepository<CvpsRequest, Long> {
//     // Custom database lookup to find records matching a specific vehicle number [cite: 379, 532, 745]
//     Optional<CvpsRequest> findByVehicleNo(String vehicleNo);
// }


package com.heg.cvps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsRequest;

@Repository
public interface CvpsRequestRepository extends JpaRepository<CvpsRequest, Long> {
    
    // Finds a request by vehicle number
    Optional<CvpsRequest> findByVehicleNo(String vehicleNo);

    // 🆕 PHASE 9: Filter requests by workflow state (e.g., show all 'CREATED' to the Confirmer)
    List<CvpsRequest> findByReqStatus(String reqStatus);

    // 🆕 PHASE 9: Security Gate Pass Verification Query
    // Finds if a vehicle has an active, fully cleared gate pass
    Optional<CvpsRequest> findByVehicleNoAndReqStatus(String vehicleNo, String reqStatus);
}