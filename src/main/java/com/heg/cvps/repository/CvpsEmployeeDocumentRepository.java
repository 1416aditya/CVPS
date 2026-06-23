package com.heg.cvps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsEmployeeDocument;

@Repository
public interface CvpsEmployeeDocumentRepository extends JpaRepository<CvpsEmployeeDocument, Long> {
}