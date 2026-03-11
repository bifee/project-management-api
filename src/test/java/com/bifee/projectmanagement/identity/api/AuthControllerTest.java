package com.bifee.projectmanagement.identity.api;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public AuthControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.token = "";
    }

    @BeforeEach
    void setUp() throws Exception {
        var registerRequest = new UserRegistrationRequest("Manager", "manager@test.com", "Password@123", UserRole.ADMIN);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        var loginRequest = new UserLoginRequest("manager@test.com", "Password@123");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        this.token = objectMapper.readTree(response).get("token").asString();
    }



}