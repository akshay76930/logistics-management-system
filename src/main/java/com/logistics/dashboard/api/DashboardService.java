package com.logistics.dashboard.api;

import com.logistics.dashboard.record.DashboardRecord;
import com.logistics.dashboard.record.DashboardRecordRepository;
import com.logistics.dashboard.record.RecordStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final DashboardRecordRepository repository;

    public DashboardService(DashboardRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        long pending = repository.countByRecordStatus(RecordStatus.PENDING);
        long inProgress = repository.countByRecordStatus(RecordStatus.IN_PROGRESS);
        long completed = repository.countByRecordStatus(RecordStatus.COMPLETED);
        long total = pending + inProgress + completed;
        return new DashboardSummaryResponse(total, pending, inProgress, completed);
    }

    @Transactional(readOnly = true)
    public List<DashboardDetailResponse> getAllRecords() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDetail)
                .toList();
    }

    private DashboardDetailResponse toDetail(DashboardRecord record) {
        return new DashboardDetailResponse(
                record.getId(),
                record.getTally(),
                record.getVehicle(),
                record.getRps(),
                record.getWeight(),
                record.getPackages(),
                record.getAssignedEmployee(),
                record.getDockName(),
                record.getVehicleStatus(),
                record.getRecordStatus(),
                record.getCreatedAt());
    }
}
