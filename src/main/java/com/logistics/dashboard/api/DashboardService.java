package com.logistics.dashboard.api;

import com.logistics.dashboard.record.DashboardRecord;
import com.logistics.dashboard.record.DashboardRecordRepository;
import com.logistics.dashboard.record.RecordStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final DashboardRecordRepository repository;

    public DashboardService(DashboardRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        long pending = repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.PENDING, startOfDay, startOfNextDay);
        long inProgress = repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.IN_PROGRESS, startOfDay, startOfNextDay);
        long completed = repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.COMPLETED, startOfDay, startOfNextDay);
        long total = repository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                startOfDay, startOfNextDay);
        return new DashboardSummaryResponse(total, pending, inProgress, completed);
    }

    @Transactional(readOnly = true)
    public List<DashboardDetailResponse> getAllRecords() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDetail)
                .toList();
    }

    @Transactional
    public DashboardDetailResponse markComplete(Long id) {
        DashboardRecord record = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found."));
        if (record.getRecordStatus() != RecordStatus.COMPLETED) {
            record.setRecordStatus(RecordStatus.COMPLETED);
            repository.save(record);
        }
        return toDetail(record);
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
