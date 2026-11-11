package com.logistics.dashboard.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
}
