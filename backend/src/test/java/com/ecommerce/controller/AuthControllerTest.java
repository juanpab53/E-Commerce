package com.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.identity.application.LoginUseCase;
import com.ecommerce.identity.infrastructure.security.LoginResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private LoginUseCase loginUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Success case: Login with valid credentials")
    void loginSuccessful() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "123456");

        when(loginUseCase.execute(any(LoginRequestDTO.class)))
                .thenReturn(new LoginResponseDTO("Juan", "token-123", "Login successful"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-123"))
                .andExpect(jsonPath("$.username").value("Juan"));
    }

    @Test
    @DisplayName("Error case: User not found (400/Exception)")
    void loginUserNotFound() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("noexiste@test.com", "123456");

        when(loginUseCase.execute(any(LoginRequestDTO.class)))
                .thenThrow(new BusinessRuleException("Invalid credentials: User not found"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Error case: Incorrect password")
    void loginWrongPassword() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "wrong_pass");

        when(loginUseCase.execute(any(LoginRequestDTO.class)))
                .thenThrow(new BusinessRuleException("Invalid credentials: Incorrect password"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Error case: Empty JSON body for required fields (400)")
    void loginRequiredFieldsMissing() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Error case: Malformed JSON (400)")
    void loginMalformedJson() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"))
                .andExpect(status().isBadRequest());
    }
}
