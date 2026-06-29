


package com.heg.cvps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsVehicleDocument;

@Repository
public interface CvpsVehicleDocumentRepository extends JpaRepository<CvpsVehicleDocument, Long> {

    /**
     * Finds an uploaded vehicle document metadata record using the parent Request Number 
     * and the explicit Document Type (e.g., 'RC', 'INSURANCE', 'PUC', 'FITNESS').
     */
    Optional<CvpsVehicleDocument> findByRequest_RequestNoAndDocumentType(Long requestNo, String documentType);
}