package com.heg.cvps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsRequestHistory;

@Repository
public interface CvpsRequestHistoryRepository extends JpaRepository<CvpsRequestHistory, Long> {
    // Inherits standard save capabilities for our audit ledger records
}