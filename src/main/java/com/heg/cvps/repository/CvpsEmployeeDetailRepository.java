package com.heg.cvps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsEmployeeDetail;

@Repository
public interface CvpsEmployeeDetailRepository extends JpaRepository<CvpsEmployeeDetail, Long> {
}