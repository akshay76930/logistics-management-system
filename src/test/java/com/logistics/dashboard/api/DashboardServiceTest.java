package com.logistics.dashboard.api;

import com.logistics.dashboard.record.DashboardRecord;
import com.logistics.dashboard.record.DashboardRecordRepository;
import com.logistics.dashboard.record.RecordStatus;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class DashboardServiceTest {

    @Test
    void summaryCountsLifecycleStatuses() {
        DashboardRecordRepository repository = mock(DashboardRecordRepository.class);
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        when(repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.PENDING, startOfDay, startOfNextDay)).thenReturn(2L);
        when(repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.IN_PROGRESS, startOfDay, startOfNextDay)).thenReturn(3L);
        when(repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.COMPLETED, startOfDay, startOfNextDay)).thenReturn(5L);
        when(repository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                startOfDay, startOfNextDay)).thenReturn(10L);
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

    @Test
    void markCompletePersistsCompletedStatus() {
        DashboardRecordRepository repository = mock(DashboardRecordRepository.class);
        DashboardRecord record = new DashboardRecord(
                "LD-1", "Truck 1", "RPS-1", BigDecimal.ONE, 1, "Employee", "Dock 1",
                "READY", RecordStatus.IN_PROGRESS, LocalDateTime.parse("2026-01-01T10:00:00"));
        record.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(record));
        when(repository.save(record)).thenReturn(record);
        DashboardService service = new DashboardService(repository);

        DashboardDetailResponse response = service.markComplete(1L);

        assertEquals(RecordStatus.COMPLETED, response.recordStatus());
        verify(repository).save(record);
    }

    @Test
    void markCompleteReturnsNotFoundForUnknownRecord() {
        DashboardRecordRepository repository = mock(DashboardRecordRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        DashboardService service = new DashboardService(repository);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.markComplete(99L));

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }
}
