package com.icesi.bu_app.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@ActiveProfiles("test")
class JwtAuthFlowIntegrationTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void loginAndMeExposeFrontendFriendlyIdentity() throws Exception {
        String token = loginAndExtractToken("juan.trainer@icesi.edu.co", "trainer123");

        mockMvc.perform(get("/rest/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(jsonPath("$.email").value("juan.trainer@icesi.edu.co"))
                .andExpect(jsonPath("$.role").value("TRAINER"))
                .andExpect(jsonPath("$.authorities").isArray())
                .andExpect(jsonPath("$.authorities[?(@ == 'ROLE_TRAINER')]").exists())
                .andExpect(jsonPath("$.authorities[?(@ == 'CREATE_ROUTINE')]").exists())
                .andExpect(jsonPath("$.authorities[?(@ == 'VIEW_PROGRESS')]").exists());
    }

    @Test
    void trainerTokenCanAccessAssignedTraineeResources() throws Exception {
        String token = loginAndExtractToken("juan.trainer@icesi.edu.co", "trainer123");

        mockMvc.perform(get("/rest/routines/trainee/4")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/progresses/search")
                        .header("Authorization", "Bearer " + token)
                        .param("from", "2024-10-01T00:00:00")
                        .param("to", "2024-10-10T23:59:59")
                        .param("traineeId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    void traineeTokenCannotAccessAnotherTraineeRoutines() throws Exception {
        String token = loginAndExtractToken("carlos@icesi.edu.co", "pass123");

        mockMvc.perform(get("/rest/routines/trainee/5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String loginAndExtractToken(String username, String password) throws Exception {
        String requestBody = objectMapper.createObjectNode()
                .put("username", username)
                .put("password", password)
                .toString();

        MvcResult result = mockMvc.perform(post("/rest/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("token").asText();
    }
}
