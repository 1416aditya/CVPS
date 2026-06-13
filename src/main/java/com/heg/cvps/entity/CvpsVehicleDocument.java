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

@Entity
@Table(name = "CVPS_VEHICLE_DOCUMENTS")
public class CvpsVehicleDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "veh_doc_seq")
    @SequenceGenerator(name = "veh_doc_seq", sequenceName = "SEQ_CVPS_VEHICLE_DOC", allocationSize = 1)
    private Long id;

    // 🔄 Added @JsonBackReference to prevent infinite JSON serialization loops
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_NO", nullable = false)
    @JsonBackReference
    private CvpsRequest request;

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    private String documentType;

    @Column(name = "DOCUMENT_NO", nullable = false)
    private String documentNo;

    @Column(name = "VALID_FROM", nullable = false)
    private LocalDate validFrom;

    @Column(name = "VALID_TILL")
    private LocalDate validTill;

    @Column(name = "FILENAME")
    private String filename;

    public CvpsVehicleDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CvpsRequest getRequest() { return request; }
    public void setRequest(CvpsRequest request) { this.request = request; }
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