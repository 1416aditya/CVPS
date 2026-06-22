package com.heg.cvps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heg.cvps.entity.CvpsUser;

@Repository
public interface CvpsUserRepository extends JpaRepository<CvpsUser, Long> {
    // Finds an active user by employee number to authorize operations
    Optional<CvpsUser> findByEmpNoAndActive(Long empNo, String active);
}