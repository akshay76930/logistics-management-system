package com.logistics.dashboard.record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardRecordRepository extends JpaRepository<DashboardRecord, Long> {

    long countByRecordStatus(RecordStatus recordStatus);

    List<DashboardRecord> findAllByOrderByCreatedAtDesc();
}
