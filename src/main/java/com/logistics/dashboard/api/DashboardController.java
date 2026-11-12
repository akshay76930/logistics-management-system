package com.logistics.dashboard.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/details")
    public List<DashboardDetailResponse> getAllRecords() {
        return dashboardService.getAllRecords();
    }

    @PostMapping("/{id}/mark-complete")
    public DashboardDetailResponse markComplete(@PathVariable Long id) {
        return dashboardService.markComplete(id);
    }
}
