package com.logistics.dashboard.record;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class DashboardRecordRepositoryTest {

    @Autowired
    private DashboardRecordRepository repository;

    @Test
    void createsTableAndReturnsRecordsFromEveryDateNewestFirst() {
        repository.save(new DashboardRecord(
                "OLDER", "Truck 1", null, BigDecimal.ONE, 1, null, null, null,
                RecordStatus.PENDING, LocalDateTime.parse("2020-01-01T10:00:00")));
        repository.save(new DashboardRecord(
                "NEWER", "Truck 2", null, BigDecimal.TEN, 2, null, null, null,
                RecordStatus.COMPLETED, LocalDateTime.now()));

        var records = repository.findAllByOrderByCreatedAtDesc();
        assertEquals(2, records.size());
        assertEquals("NEWER", records.get(0).getTally());
        assertEquals("OLDER", records.get(1).getTally());
    }

    @Test
    void summaryCountsOnlyRecordsCreatedToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        repository.save(new DashboardRecord(
                "TODAY-START", "Truck 1", null, BigDecimal.ONE, 1, null, null, null,
                RecordStatus.PENDING, startOfDay));
        repository.save(new DashboardRecord(
                "TODAY-END", "Truck 2", null, BigDecimal.ONE, 1, null, null, null,
                RecordStatus.COMPLETED, LocalDateTime.now()));
        repository.save(new DashboardRecord(
                "YESTERDAY", "Truck 3", null, BigDecimal.ONE, 1, null, null, null,
                RecordStatus.PENDING, startOfDay.minusSeconds(1)));
        repository.save(new DashboardRecord(
                "TOMORROW", "Truck 4", null, BigDecimal.ONE, 1, null, null, null,
                RecordStatus.IN_PROGRESS, startOfNextDay));

        assertEquals(2, repository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                startOfDay, startOfNextDay));
        assertEquals(1, repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.PENDING, startOfDay, startOfNextDay));
        assertEquals(1, repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.COMPLETED, startOfDay, startOfNextDay));
        assertEquals(0, repository.countByRecordStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                RecordStatus.IN_PROGRESS, startOfDay, startOfNextDay));
    }
}
