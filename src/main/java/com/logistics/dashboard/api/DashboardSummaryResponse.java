package com.logistics.dashboard.api;

public record DashboardSummaryResponse(
        long total,
        long pending,
        long inProgress,
        long completed) {
}
