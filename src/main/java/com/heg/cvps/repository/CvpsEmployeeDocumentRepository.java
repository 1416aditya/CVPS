


package com.heg.cvps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsEmployeeDocument;

@Repository
public interface CvpsEmployeeDocumentRepository extends JpaRepository<CvpsEmployeeDocument, Long> {
    
    /**
     * Finds all uploaded file scans linked to a specific crew member profile.
     * Useful for showing a summary of a driver's documents on the frontend.
     */
    List<CvpsEmployeeDocument> findByEmpId(Long empId);
    
    /**
     * Finds a highly specific document type (e.g., 'PHOTOGRAPH') for a single driver.
     * Useful for pulling up a driver's profile photo at the security check gate.
     */
    List<CvpsEmployeeDocument> findByEmpIdAndDocumentType(Long empId, String documentType);
}