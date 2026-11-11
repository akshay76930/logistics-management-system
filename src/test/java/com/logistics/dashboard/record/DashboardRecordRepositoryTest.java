package com.logistics.dashboard.record;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
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
}
