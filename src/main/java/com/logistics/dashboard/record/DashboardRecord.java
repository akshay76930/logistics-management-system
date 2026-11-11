package com.logistics.dashboard.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_records")
public class DashboardRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tally;

    @Column(nullable = false)
    private String vehicle;

    @Column(name = "rps")
    private String rps;

    @Column(precision = 12, scale = 3)
    private BigDecimal weight;

    private Integer packages;

    @Column(name = "assigned_employee")
    private String assignedEmployee;

    @Column(name = "dock_name")
    private String dockName;

    @Column(name = "vehicle_status")
    private String vehicleStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_status", nullable = false)
    private RecordStatus recordStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected DashboardRecord() {
    }

    public DashboardRecord(
            String tally,
            String vehicle,
            String rps,
            BigDecimal weight,
            Integer packages,
            String assignedEmployee,
            String dockName,
            String vehicleStatus,
            RecordStatus recordStatus,
            LocalDateTime createdAt) {
        this.tally = tally;
        this.vehicle = vehicle;
        this.rps = rps;
        this.weight = weight;
        this.packages = packages;
        this.assignedEmployee = assignedEmployee;
        this.dockName = dockName;
        this.vehicleStatus = vehicleStatus;
        this.recordStatus = recordStatus;
        this.createdAt = createdAt;
    }

    @PrePersist
    void setCreatedAtIfMissing() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTally(String tally) {
        this.tally = tally;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public void setRps(String rps) {
        this.rps = rps;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setPackages(Integer packages) {
        this.packages = packages;
    }

    public void setAssignedEmployee(String assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public void setDockName(String dockName) {
        this.dockName = dockName;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTally() {
        return tally;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getRps() {
        return rps;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Integer getPackages() {
        return packages;
    }

    public String getAssignedEmployee() {
        return assignedEmployee;
    }

    public String getDockName() {
        return dockName;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
