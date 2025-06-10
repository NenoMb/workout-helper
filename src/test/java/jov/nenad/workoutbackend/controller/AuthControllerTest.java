package jov.nenad.workoutbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jov.nenad.workoutbackend.dto.ForgotPasswordRequest;
import jov.nenad.workoutbackend.dto.LoginRequest;
import jov.nenad.workoutbackend.dto.RegisterRequest;
import jov.nenad.workoutbackend.dto.ResetPasswordRequest;
import jov.nenad.workoutbackend.entity.User;
import jov.nenad.workoutbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@WithMockUser
@Import(AuthControllerTest.TestConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Configuration
    @EnableWebSecurity
    static class TestConfig {

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        // Reset mock to clear any previous interactions
        Mockito.reset(userService);

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup forgot password request
        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("test@example.com");

        // Setup reset password request
        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken("valid-token");
        resetPasswordRequest.setNewPassword("newPassword123");
    }

    @Test
    void testRegister() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenReturn(testUser);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testLogin() throws Exception {
        when(userService.login(anyString(), anyString())).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(userService, times(1)).login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @Test
    void testForgotPassword() throws Exception {
        doNothing().when(userService).forgotPassword(any(ForgotPasswordRequest.class));

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset email sent"));

        verify(userService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void testForgotPasswordWithMessagingException() throws Exception {
        doThrow(new MessagingException("Error sending email")).when(userService).forgotPassword(any(ForgotPasswordRequest.class));

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset email sent"));

        verify(userService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void testResetPassword() throws Exception {
        doNothing().when(userService).resetPassword(any(ResetPasswordRequest.class));

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));

        verify(userService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }
}
