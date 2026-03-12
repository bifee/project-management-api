package com.bifee.projectmanagement.management.api;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.domain.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {
    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private String token;

    private Long userId;

    public ProjectControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.token = "";
    }

    @BeforeEach
    void setUp() throws Exception {
        var adminRegisterRequest = new UserRegistrationRequest(
                "Manager",
                "manager@test.com",
                "Password@123",
                UserRole.ADMIN
        );

        var devRegisterRequest = new UserRegistrationRequest(
                "Developer",
                "dev@test.com",
                "DevPassword@123",
                UserRole.DEV
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegisterRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devRegisterRequest)))
                .andExpect(status().isCreated());


        var adminLoginRequest = new UserLoginRequest("manager@test.com", "Password@123");
        var devLoginRequest = new UserLoginRequest("dev@test.com", "DevPassword@123");


        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        this.token = objectMapper.readTree(response).get("token").asText();

    }

    @Test
    void getProjectById() throws Exception {
        mockMvc.perform(get("/api/projects/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectsByOwnerId() throws Exception{
    }

    @Test
    void getMyProjects() throws Exception{
    }

    @Test
    void searchProjects() throws Exception{
    }

    @Test
    void getAllTasksByProjectId() throws Exception{
    }

    @Test
    void createProject() throws Exception{
    }

    @Test
    void deleteProject() throws Exception{
    }

    @Test
    void updateProject() throws Exception{
    }

    @Test
    void addMembersToProject() throws Exception{
    }

    @Test
    void removeMemberFromProject() throws Exception{
    }

    @Test
    void createTask() throws Exception{
    }
}