package com.match.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthControllerIT extends AbstractPostgresIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void register_login_refresh_and_protected_access() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@x.com";

        // 1) Register
        MvcResult registerRes = mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "email", email,
                    "password", "Secret123",
                    "displayName", "Tester"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
            .andReturn();

        JsonNode reg = json.readTree(registerRes.getResponse().getContentAsString());
        String access = reg.get("accessToken").asText();
        String refresh = reg.get("refreshToken").asText();

        // 2) Duplicate registration → 400
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "email", email, "password", "Secret123", "displayName", "x"))))
            .andExpect(status().isBadRequest());

        // 3) Login
        MvcResult loginRes = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "password", "Secret123"))))
            .andExpect(status().isOk())
            .andReturn();
        String accessFromLogin = json.readTree(loginRes.getResponse().getContentAsString())
            .get("accessToken").asText();
        assertNotNull(accessFromLogin);

        // 4) Login with wrong password → 400 (DomainException)
        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "password", "WRONG"))))
            .andExpect(status().isBadRequest());

        // 5) Refresh
        mvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("refreshToken", refresh))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());

        // 6) Protected endpoint without token → 401
        mvc.perform(get("/api/v1/discovery").param("lat", "0").param("lng", "0"))
            .andExpect(status().isUnauthorized());

        // 7) Protected endpoint with valid token → 200
        mvc.perform(get("/api/v1/discovery")
                .header("Authorization", "Bearer " + access)
                .param("lat", "0").param("lng", "0"))
            .andExpect(status().isOk());
    }

    @Test
    void register_validation_rejects_short_password() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "email", "v@x.com", "password", "short", "displayName", "x"))))
            .andExpect(status().isBadRequest());
    }
}

