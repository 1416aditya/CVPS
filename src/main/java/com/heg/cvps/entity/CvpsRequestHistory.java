

package com.heg.cvps.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CVPS_REQUESTS_HISTORY", uniqueConstraints = {
    @UniqueConstraint(name = "UK_CVPS_HISTORY", columnNames = {"REQUEST_ID", "ACTION_TAKEN", "ACTION_DATE"})
})
public class CvpsRequestHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
    @SequenceGenerator(name = "history_seq", sequenceName = "SEQ_CVPS_HISTORY", allocationSize = 1)
    @Column(name = "HISTORY_ID")
    private Long historyId;

    // 🔄 Added @JsonBackReference to safely integrate with the parent request's serialization loop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    @JsonBackReference
    private CvpsRequest request;

    @NotNull
    @Column(name = "EMPNO", columnDefinition = "CHAR(9)", nullable = false)
    private String empNo;

    @NotNull
    @Size(max = 15)
    @Column(name = "ACTION_TAKEN", nullable = false)
    private String actionTaken;

    @Size(max = 100)
    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "ACTION_DATE", insertable = false, updatable = false)
    private LocalDateTime actionDate;

    public CvpsRequestHistory() {}

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
    
    public CvpsRequest getRequest() { return request; }
    public void setRequest(CvpsRequest request) { this.request = request; }
    
    public String getEmpNo() { return empNo; }
    public void setEmpNo(String empNo) { this.empNo = empNo; }
    
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
}