package com.logistics.dashboard.api;

import com.logistics.dashboard.record.DashboardRecord;
import com.logistics.dashboard.record.DashboardRecordRepository;
import com.logistics.dashboard.record.RecordStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void summaryCountsLifecycleStatuses() {
        DashboardRecordRepository repository = mock(DashboardRecordRepository.class);
        when(repository.countByRecordStatus(RecordStatus.PENDING)).thenReturn(2L);
        when(repository.countByRecordStatus(RecordStatus.IN_PROGRESS)).thenReturn(3L);
        when(repository.countByRecordStatus(RecordStatus.COMPLETED)).thenReturn(5L);
        DashboardService service = new DashboardService(repository);

        assertEquals(new DashboardSummaryResponse(10, 2, 3, 5), service.getSummary());
    }

    @Test
    void detailsQueryReturnsAllRecordsNewestFirst() {
        DashboardRecordRepository repository = mock(DashboardRecordRepository.class);
        LocalDateTime createdAt = LocalDateTime.parse("2020-01-01T10:00:00");
        DashboardRecord record = new DashboardRecord(
                "LD-OLD", "Truck 1", "RPS-1", BigDecimal.ONE, 1, "Employee", "Dock 1",
                "READY", RecordStatus.PENDING, createdAt);
        record.setId(1L);
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(record));
        DashboardService service = new DashboardService(repository);

        assertEquals(
                List.of(new DashboardDetailResponse(
                        1L, "LD-OLD", "Truck 1", "RPS-1", BigDecimal.ONE, 1, "Employee",
                        "Dock 1", "READY", RecordStatus.PENDING, createdAt)),
                service.getAllRecords());
        verify(repository).findAllByOrderByCreatedAtDesc();
    }
}
