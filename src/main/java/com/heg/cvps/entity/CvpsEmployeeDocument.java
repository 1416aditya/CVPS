package com.heg.cvps.entity;

import java.io.Serializable;
import java.time.LocalDate;

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
@Table(name = "CVPS_EMPLOYEE_DOCUMENTS", uniqueConstraints = {
    @UniqueConstraint(name = "UK_CVPS_EMP_DOC", columnNames = {"EMP_ID", "DOCUMENT_NO", "VALID_FROM"})
})
public class CvpsEmployeeDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_doc_seq")
    @SequenceGenerator(name = "emp_doc_seq", sequenceName = "SEQ_CVPS_EMP_DOC", allocationSize = 1)
    private Long id;

    // 🔄 Upward Link to Parent Personnel Detail Record with Jackson Loop protection
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", nullable = false)
    @JsonBackReference
    private CvpsEmployeeDetail employeeDetail;

    @Column(name = "DOCUMENT_TYPE", nullable = false, length = 20)
    private String documentType;

    @Column(name = "DOCUMENT_NO", nullable = false, length = 25)
    private String documentNo;

    @Column(name = "VALID_FROM", nullable = false)
    private LocalDate validFrom;

    @Column(name = "VALID_TILL")
    private LocalDate validTill;

    @Column(name = "FILENAME", length = 50)
    private String filename;

    public CvpsEmployeeDocument() {}

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CvpsEmployeeDetail getEmployeeDetail() { return employeeDetail; }
    public void setEmployeeDetail(CvpsEmployeeDetail employeeDetail) { this.employeeDetail = employeeDetail; }

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