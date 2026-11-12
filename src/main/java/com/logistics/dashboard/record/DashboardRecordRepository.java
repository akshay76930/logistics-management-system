package com.logistics.dashboard.record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRecordRepository extends JpaRepository<DashboardRecord, Long> {

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime startOfDay,
            LocalDateTime startOfNextDay);

    long countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            RecordStatus recordStatus,
            LocalDateTime startOfDay,
            LocalDateTime startOfNextDay);

    List<DashboardRecord> findAllByOrderByCreatedAtDesc();
}
