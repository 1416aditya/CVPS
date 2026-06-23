package com.heg.cvps.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "CVPS_EMPLOYEE_DOCUMENTS")
public class CvpsEmployeeDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_doc_seq")
    @SequenceGenerator(name = "emp_doc_seq", sequenceName = "SEQ_CVPS_EMP_DOC_NO", allocationSize = 1)
    private Long id;

    @Column(name = "EMP_ID", nullable = false)
    private Long empId;

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    private String documentType; 

    @Column(name = "DOCUMENT_NO")
    private String documentNo; 

    @Column(name = "VALID_FROM")
    private LocalDate validFrom;

    @Column(name = "VALID_TILL")
    private LocalDate validTill;

    @Column(name = "FILENAME", nullable = false)
    private String filename; 

    public CvpsEmployeeDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getDocumentNo() { return documentNo; }
    public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTill() { return validTill; }
    public void setValidTill(LocalDate validTill) { this.validTill = validTill; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
}