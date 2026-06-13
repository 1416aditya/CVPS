// package com.heg.cvps.entity;

// import java.io.Serializable;
// import java.time.LocalDateTime;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.SequenceGenerator;
// import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;

// @Entity
// @Table(name = "CVPS_REQUESTS", uniqueConstraints = {
//     @UniqueConstraint(name = "UK_CVPS_REQ", columnNames = {"CONTRACTOR_ID", "VEHICLE_NO", "PERMISSION_FROM"})
// })
// public class CvpsRequest implements Serializable {

//     private static final long serialVersionUID = 1L;

//     // System-generated unique Request Number (Primary Key) via your PDF Sequence
//     @Id
//     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cvps_req_seq")
//     @SequenceGenerator(name = "cvps_req_seq", sequenceName = "SEQ_CVPS_REQUEST_NO", allocationSize = 1)
//     @Column(name = "REQUEST_NO")
//     private Long requestNo;

//     // Contractor employee/vendor code submitting the request
//     @NotNull
//     @Column(name = "CONTRACTOR_ID", columnDefinition = "CHAR(9)", nullable = false)
//     private String contractorId;

//     // Purpose of vehicle entry (Material Delivery, Maintenance, etc.)
//     @NotNull
//     @Size(max = 150)
//     @Column(name = "NATURE_OF_JOB", nullable = false)
//     private String natureOfJob;

//     // Vehicle registration number
//     @NotNull
//     @Size(max = 20)
//     @Column(name = "VEHICLE_NO", nullable = false)
//     private String vehicleNo;

//     // Category values: TRUCK, CRANE, TRAILER, PICKUP, FORKLIFT
//     @NotNull
//     @Size(max = 10)
//     @Column(name = "VEHICLE_TYPE", nullable = false)
//     private String vehicleType;

//     // Start date and time of vehicle entry permission
//     @NotNull
//     @Column(name = "PERMISSION_FROM", nullable = false)
//     private LocalDateTime permissionFrom;

//     // End date and time of vehicle entry permission
//     @NotNull
//     @Column(name = "PERMISSION_TO", nullable = false)
//     private LocalDateTime permissionTo;

//     // Current request status. Default value is 'CREATED'
//     @NotNull
//     @Size(max = 20)
//     @Column(name = "REQ_STATUS", nullable = false)
//     private String reqStatus = "CREATED";

//     // Employee/Contractor ID who created the request
//     @NotNull
//     @Column(name = "CREATED_BY", columnDefinition = "CHAR(9)", nullable = false)
//     private String createdBy;

//     // Date and time when the request was created (Defaults to SYSDATE in DB)
//     @Column(name = "CREATED_DATE", insertable = false, updatable = false)
//     private LocalDateTime createdDate;

//     // Default No-Arg Constructor required by JPA
//     public CvpsRequest() {}

//     // Clear, Standard Getters and Setters
//     public Long getRequestNo() { return requestNo; }
//     public void setRequestNo(Long requestNo) { this.requestNo = requestNo; }

//     public String getContractorId() { return contractorId; }
//     public void setContractorId(String contractorId) { this.contractorId = contractorId; }

//     public String getNatureOfJob() { return natureOfJob; }
//     public void setNatureOfJob(String natureOfJob) { this.natureOfJob = natureOfJob; }

//     public String getVehicleNo() { return vehicleNo; }
//     public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

//     public String getVehicleType() { return vehicleType; }
//     public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

//     public LocalDateTime getPermissionFrom() { return permissionFrom; }
//     public void setPermissionFrom(LocalDateTime permissionFrom) { this.permissionFrom = permissionFrom; }

//     public LocalDateTime getPermissionTo() { return permissionTo; }
//     public void setPermissionTo(LocalDateTime permissionTo) { this.permissionTo = permissionTo; }

//     public String getReqStatus() { return reqStatus; }
//     public void setReqStatus(String reqStatus) { this.reqStatus = reqStatus; }

//     public String getCreatedBy() { return createdBy; }
//     public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

//     public LocalDateTime getCreatedDate() { return createdDate; }
//     public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
// }


package com.heg.cvps.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CVPS_REQUESTS", uniqueConstraints = {
    @UniqueConstraint(name = "UK_CVPS_REQ", columnNames = {"CONTRACTOR_ID", "VEHICLE_NO", "PERMISSION_FROM"})
})
public class CvpsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cvps_req_seq")
    @SequenceGenerator(name = "cvps_req_seq", sequenceName = "SEQ_CVPS_REQUEST_NO", allocationSize = 1)
    @Column(name = "REQUEST_NO")
    private Long requestNo;

    @NotNull
    @Column(name = "CONTRACTOR_ID", columnDefinition = "CHAR(9)", nullable = false)
    private String contractorId;

    @NotNull
    @Size(max = 150)
    @Column(name = "NATURE_OF_JOB", nullable = false)
    private String natureOfJob;

    @NotNull
    @Size(max = 20)
    @Column(name = "VEHICLE_NO", nullable = false)
    private String vehicleNo;

    @NotNull
    @Size(max = 10)
    @Column(name = "VEHICLE_TYPE", nullable = false)
    private String vehicleType;

    @NotNull
    @Column(name = "PERMISSION_FROM", nullable = false)
    private LocalDateTime permissionFrom;

    @NotNull
    @Column(name = "PERMISSION_TO", nullable = false)
    private LocalDateTime permissionTo;

    @NotNull
    @Size(max = 20)
    @Column(name = "REQ_STATUS", nullable = false)
    private String reqStatus = "CREATED";

    @NotNull
    @Column(name = "CREATED_BY", columnDefinition = "CHAR(9)", nullable = false)
    private String createdBy;

    @Column(name = "CREATED_DATE", insertable = false, updatable = false)
    private LocalDateTime createdDate;

    // 🔄 1. Relationship to Vehicle Documents (Phase 4)
    @OneToMany(mappedBy = "request", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CvpsVehicleDocument> vehicleDocuments;

    // 🔄 2. Relationship to Workflow Audit History Trail (Phase 8) - Fixed Naming Mismatch
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CvpsRequestHistory> requestHistories;

    // 🔄 3. Relationship to Drivers and Associated Personnel (Phase 5)
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CvpsEmployeeDetail> employeeDetails;

    public CvpsRequest() {}

    // Clear, Standard Getters and Setters
    public Long getRequestNo() { return requestNo; }
    public void setRequestNo(Long requestNo) { this.requestNo = requestNo; }

    public String getContractorId() { return contractorId; }
    public void setContractorId(String contractorId) { this.contractorId = contractorId; }

    public String getNatureOfJob() { return natureOfJob; }
    public void setNatureOfJob(String natureOfJob) { this.natureOfJob = natureOfJob; }

    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public LocalDateTime getPermissionFrom() { return permissionFrom; }
    public void setPermissionFrom(LocalDateTime permissionFrom) { this.permissionFrom = permissionFrom; }

    public LocalDateTime getPermissionTo() { return permissionTo; }
    public void setPermissionTo(LocalDateTime permissionTo) { this.permissionTo = permissionTo; }

    public String getReqStatus() { return reqStatus; }
    public void setReqStatus(String reqStatus) { this.reqStatus = reqStatus; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    // Relationship Getters and Setters
    public List<CvpsVehicleDocument> getVehicleDocuments() { return vehicleDocuments; }
    public void setVehicleDocuments(List<CvpsVehicleDocument> vehicleDocuments) { this.vehicleDocuments = vehicleDocuments; }

    public List<CvpsRequestHistory> getRequestHistories() { return requestHistories; }
    public void setRequestHistories(List<CvpsRequestHistory> requestHistories) { this.requestHistories = requestHistories; }

    public List<CvpsEmployeeDetail> getEmployeeDetails() { return employeeDetails; }
    public void setEmployeeDetails(List<CvpsEmployeeDetail> employeeDetails) { this.employeeDetails = employeeDetails; }
}