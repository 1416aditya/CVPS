package com.heg.cvps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsRequest;

@Repository
public interface CvpsRequestRepository extends JpaRepository<CvpsRequest, Long> {
    // Custom database lookup to find records matching a specific vehicle number [cite: 379, 532, 745]
    Optional<CvpsRequest> findByVehicleNo(String vehicleNo);
}