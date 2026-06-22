package com.heg.cvps.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CVPS_USER")
public class CvpsUser {

    @Id
    @Column(name = "EMPNO")
    private Long empNo;

    @Column(name = "EMP_TYPE", nullable = false, length = 20)
    private String empType;

    @Column(name = "ROLES", nullable = false, length = 30)
    private String roles;

    @Column(name = "ACTIVE", nullable = false, length = 1)
    private String active = "Y";

    // Getters and Setters
    public Long getEmpNo() {
        return empNo;
    }
    public void setEmpNo(Long empNo) {
        this.empNo = empNo;
    }

    public String getEmpType() {
        return empType;
    }
    public void setEmpType(String empType) {
        this.empType = empType;
    }

    public String getRoles() {
        return roles;
    }
    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }
}