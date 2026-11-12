package com.logistics.dashboard.auth;

import com.logistics.dashboard.record.DashboardRecord;
import com.logistics.dashboard.record.DashboardRecordRepository;
import com.logistics.dashboard.record.RecordStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:local-profile-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class LocalProfileSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DashboardRecordRepository dashboardRecordRepository;

    @AfterEach
    void cleanUpRecords() {
        dashboardRecordRepository.deleteAll();
    }

    @Test
    void dashboardCanBeLoadedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void localLoginReturnsDevelopmentTokenForNonBlankCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"local-user\",\"password\":\"local-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("local-development"));
    }

    @Test
    void markCompleteEndpointUpdatesRecordStatus() throws Exception {
        DashboardRecord record = dashboardRecordRepository.save(new DashboardRecord(
                "LD-1", "Truck 1", "RPS-1", null, 2, "Employee", "Dock 1",
                "READY", RecordStatus.IN_PROGRESS, LocalDateTime.now()));

        mockMvc.perform(post("/api/dashboard/{id}/mark-complete", record.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordStatus").value("COMPLETED"));
    }
}
