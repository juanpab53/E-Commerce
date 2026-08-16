package com.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import com.ecommerce.dto.UserRegistrationDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.identity.application.RegisterUserUseCase;
import com.ecommerce.service.UserService;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /users/register - Should register a user")
    void registerUser() throws Exception {
        UserRegistrationDTO registration = new UserRegistrationDTO(
                "Juan", "Perez", "juan@test.com", "123456789", "Colombia",
                "Bogota", "Calle 1", null);
        UserResponseDTO response = new UserResponseDTO(1L, "Juan", "juan@test.com", "Colombia, Bogota, Calle 1",
                "CUSTOMER");

        when(registerUserUseCase.execute(any(UserRegistrationDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    @DisplayName("GET /users - Should list all users")
    void listUsers() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Juan", "juan@test.com", "Colombia, Bogota, Calle 1",
                "CUSTOMER");
        when(userService.listUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan"));
    }

    @Test
    @DisplayName("GET /users/{id} - Should find a user by ID")
    void findById() throws Exception {
        UserResponseDTO response = new UserResponseDTO(1L, "Juan", "juan@test.com", "Colombia, Bogota, Calle 1",
                "CUSTOMER");
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("PUT /users/{id} - Should update a user")
    void updateUser() throws Exception {
        UserRegistrationDTO update = new UserRegistrationDTO("JuanMod", "PerezMod", "juan@test.com", "123456789",
                "Colombia", "Bogota", "Calle 1", null);
        UserResponseDTO response = new UserResponseDTO(1L, "JuanMod", "juan@test.com",
                "Colombia, Bogota, Calle 1",
                "CUSTOMER");

        when(userService.update(anyLong(), any(UserRegistrationDTO.class))).thenReturn(response);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("JuanMod"));
    }

    @Test
    @DisplayName("DELETE /users/{id} - Should delete a user")
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
