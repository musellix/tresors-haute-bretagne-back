package com.tresorshautebretagne.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tresorshautebretagne.auth.dto.AuthResponse;
import com.tresorshautebretagne.auth.dto.LoginRequest;
import com.tresorshautebretagne.auth.dto.RefreshTokenRequest;
import com.tresorshautebretagne.auth.dto.RegisterRequest;
import com.tresorshautebretagne.config.JwtService;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserDTO;
import com.tresorshautebretagne.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
class AuthControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testChain(HttpSecurity http) throws Exception {
            return http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                    .build();
        }
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;
    @MockBean MapperService mapperService;
    @MockBean JwtService jwtService;
    @MockBean UserRepository userRepository;

    private AuthResponse buildAuthResponse() {
        return AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .user(new UserDTO(1L, "user@test.com", "Test User", null))
                .build();
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    void register_returns200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");
        request.setName("Test User");

        doNothing().when(authService).register(any());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void register_returns400_whenBodyInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword("short");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns409_whenEmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");
        request.setName("Test User");

        doThrow(new RuntimeException("EMAIL_ALREADY_EXISTS")).when(authService).register(any());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_returns200WithAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        when(authService.login(any())).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_returns401_whenBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong");

        when(authService.login(any())).thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_returns403_whenEmailNotVerified() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        when(authService.login(any())).thenThrow(new RuntimeException("EMAIL_NOT_VERIFIED"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ── refresh ───────────────────────────────────────────────────────────────

    @Test
    void refresh_returns200WithNewTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-token");

        when(authService.refresh("old-token")).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void refresh_returns401_whenTokenInvalidOrExpired() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("bad-token");

        when(authService.refresh("bad-token")).thenThrow(new RuntimeException("INVALID_REFRESH_TOKEN"));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test
    void logout_returns200() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("some-token");

        doNothing().when(authService).logout("some-token");

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ── verifyEmail ───────────────────────────────────────────────────────────

    @Test
    void verifyEmail_returns200() throws Exception {
        doNothing().when(authService).verifyEmail("verify-token");

        mockMvc.perform(get("/auth/verify-email").param("token", "verify-token"))
                .andExpect(status().isOk());
    }

    @Test
    void verifyEmail_returns410_whenTokenExpired() throws Exception {
        doThrow(new RuntimeException("VERIFICATION_TOKEN_EXPIRED")).when(authService).verifyEmail("expired");

        mockMvc.perform(get("/auth/verify-email").param("token", "expired"))
                .andExpect(status().isGone());
    }

    // ── resendVerification ────────────────────────────────────────────────────

    @Test
    void resendVerification_returns200() throws Exception {
        doNothing().when(authService).resendVerification("user@test.com");

        mockMvc.perform(post("/auth/resend-verification").param("email", "user@test.com"))
                .andExpect(status().isOk());
    }

    // ── me ────────────────────────────────────────────────────────────────────

    @Test
    void me_returns200WithUserDTO() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setName("Test User");
        user.setEmailVerified(true);

        when(mapperService.userToDTO(user)).thenReturn(new UserDTO(1L, "user@test.com", "Test User", null));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

        mockMvc.perform(get("/auth/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
