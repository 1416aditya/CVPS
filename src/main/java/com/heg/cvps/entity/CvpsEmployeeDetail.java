


package com.heg.cvps.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "CVPS_EMPLOYEE_DETAIL", uniqueConstraints = {
    @UniqueConstraint(name = "UK_CVPS_EMPLOYEE", columnNames = {"REQ_ID", "AADHAR_NO"})
})
public class CvpsEmployeeDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_det_seq")
    @SequenceGenerator(name = "emp_det_seq", sequenceName = "SEQ_CVPS_EMPLOYEE_DETAIL", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQ_ID", nullable = false)
    @JsonBackReference
    private CvpsRequest request;

    @Column(name = "EMP_JOB", nullable = false)
    private String empJob;

    @Column(name = "EMP_TYPE", nullable = false)
    private String empType;

    @Column(name = "EMP_NO")
    private Long empNo;

    @Column(name = "AADHAR_NO")
    private String aadharNo;

    @Column(name = "NAME")
    private String name;

    // 🟢 ADDED: Correct relational mappings for the updated database columns
    @Column(name = "MOBILE_NO", length = 20)
    private String mobileNo;

    @Column(name = "DRIVER_PHOTO_NAME", length = 255)
    private String driverPhotoName;

    public CvpsEmployeeDetail() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public CvpsRequest getRequest() { return request; }
    public void setRequest(CvpsRequest request) { this.request = request; }
    
    public String getEmpJob() { return empJob; }
    public void setEmpJob(String empJob) { this.empJob = empJob; }
    
    public String getEmpType() { return empType; }
    public void setEmpType(String empType) { this.empType = empType; }
    
    public Long getEmpNo() { return empNo; }
    public void setEmpNo(Long empNo) { this.empNo = empNo; }
    
    public String getAadharNo() { return aadharNo; }
    public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // ── Getters and Setters for the new schema parameters ──
    public String getMobileNo() { 
        return mobileNo; 
    }
    public void setMobileNo(String mobileNo) { 
        this.mobileNo = mobileNo; 
    }
    
    public String getDriverPhotoName() { 
        return driverPhotoName; 
    }
    public void setDriverPhotoName(String driverPhotoName) { 
        this.driverPhotoName = driverPhotoName; 
    }
}