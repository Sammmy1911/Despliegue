package com.icesi.bu_app.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.icesi.bu_app.service.IPermissionService;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
@Import(PreAuthorizeIntegrationTests.TestSecurityConfig.class)
public class PreAuthorizeIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private IPermissionService permissionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = {"TRAINER"})
    void trainerCanAccessTraineeRoutinesWhenPermissionServiceAllows() throws Exception {
        when(permissionService.canViewTrainee(any(), eq(42))).thenReturn(true);

        mockMvc.perform(get("/rest/routines/trainee/42")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"TRAINER"})
    void trainerForbiddenWhenPermissionServiceDenies() throws Exception {
        when(permissionService.canViewTrainee(any(), eq(99))).thenReturn(false);

        mockMvc.perform(get("/rest/routines/trainee/99")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void adminCanSearchProgressWithoutTraineeId() throws Exception {
        when(permissionService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(get("/rest/progresses/search").param("from", "2024-01-01T00:00:00").param("to", "2024-01-02T00:00:00")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"TRAINEE"})
    void nonAdminSearchWithoutTraineeIdForbidden() throws Exception {
        when(permissionService.isAdmin(any())).thenReturn(false);

        mockMvc.perform(get("/rest/progresses/search").param("from", "2024-01-01T00:00:00").param("to", "2024-01-02T00:00:00")).andExpect(status().isForbidden());
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        @Primary
        IPermissionService permissionService() {
            return mock(IPermissionService.class);
        }
    }
}
