package com.logistics.dashboard.api;

import com.logistics.dashboard.record.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DashboardDetailResponse(
        Long id,
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
}
