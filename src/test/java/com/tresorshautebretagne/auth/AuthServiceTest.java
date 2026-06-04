package com.tresorshautebretagne.auth;

import com.tresorshautebretagne.auth.dto.AuthResponse;
import com.tresorshautebretagne.auth.dto.LoginRequest;
import com.tresorshautebretagne.auth.dto.RegisterRequest;
import com.tresorshautebretagne.auth.token.EmailVerificationToken;
import com.tresorshautebretagne.auth.token.EmailVerificationTokenRepository;
import com.tresorshautebretagne.auth.token.RefreshToken;
import com.tresorshautebretagne.auth.token.RefreshTokenRepository;
import com.tresorshautebretagne.config.JwtService;
import com.tresorshautebretagne.email.EmailService;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserDTO;
import com.tresorshautebretagne.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EmailService emailService;
    @Mock private MapperService mapperService;

    @InjectMocks
    private AuthService authService;

    private static final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshExpiration", 604800000L);
        ReflectionTestUtils.setField(authService, "googleClientId", "fake-client-id");
    }

    private User buildVerifiedUser() {
        User u = new User();
        u.setId(1L);
        u.setEmail(EMAIL);
        u.setName("Test User");
        u.setEmailVerified(true);
        return u;
    }

    private User buildUnverifiedUser() {
        User u = buildVerifiedUser();
        u.setEmailVerified(false);
        return u;
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    void register_savesUserWithEncodedPasswordAndSendsVerificationEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("  Test@Example.COM  ");
        request.setPassword("password123");
        request.setName("Test User");

        User saved = buildUnverifiedUser();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(saved);

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("test@example.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(captor.getValue().getEmailVerified()).isFalse();
        verify(emailService).sendVerificationEmail(eq(EMAIL), anyString(), anyString());
    }

    @Test
    void register_throws_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(EMAIL);
        request.setPassword("password123");
        request.setName("Test");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(buildVerifiedUser()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("EMAIL_ALREADY_EXISTS");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_returnsAuthResponse_whenCredentialsValidAndEmailVerified() {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword("password123");

        User user = buildVerifiedUser();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(1L)).thenReturn("access-token");
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(mapperService.userToDTO(user)).thenReturn(new UserDTO(1L, EMAIL, "Test User", null));

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isNotNull();
    }

    @Test
    void login_throws_whenEmailNotVerified() {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword("password123");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(buildUnverifiedUser()));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("EMAIL_NOT_VERIFIED");
    }

    @Test
    void login_throws_whenBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword("wrong");

        doThrow(new BadCredentialsException("bad")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ── refresh ───────────────────────────────────────────────────────────────

    @Test
    void refresh_revokesOldTokenAndReturnsNewAuthResponse() {
        User user = buildVerifiedUser();
        RefreshToken token = RefreshToken.builder()
                .id(1L).token("old-token").user(user).revoked(false)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(jwtService.generateToken(1L)).thenReturn("new-access");
        when(mapperService.userToDTO(user)).thenReturn(new UserDTO());

        AuthResponse response = authService.refresh("old-token");

        assertThat(token.getRevoked()).isTrue();
        assertThat(response.getAccessToken()).isEqualTo("new-access");
        verify(refreshTokenRepository, times(2)).save(any());
    }

    @Test
    void refresh_throws_whenTokenNotFound() {
        when(refreshTokenRepository.findByToken("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("bad"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("INVALID_REFRESH_TOKEN");
    }

    @Test
    void refresh_throws_whenTokenRevoked() {
        RefreshToken token = RefreshToken.builder()
                .token("revoked").user(buildVerifiedUser()).revoked(true)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenRepository.findByToken("revoked")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.refresh("revoked"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("REFRESH_TOKEN_EXPIRED");
    }

    @Test
    void refresh_throws_whenTokenExpired() {
        RefreshToken token = RefreshToken.builder()
                .token("expired").user(buildVerifiedUser()).revoked(false)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.refresh("expired"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("REFRESH_TOKEN_EXPIRED");
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test
    void logout_revokesToken() {
        RefreshToken token = RefreshToken.builder()
                .token("some-token").user(buildVerifiedUser()).revoked(false)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenRepository.findByToken("some-token")).thenReturn(Optional.of(token));

        authService.logout("some-token");

        assertThat(token.getRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void logout_doesNothing_whenTokenNotFound() {
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        authService.logout("unknown");

        verify(refreshTokenRepository, never()).save(any());
    }

    // ── verifyEmail ───────────────────────────────────────────────────────────

    @Test
    void verifyEmail_marksUserAsVerified_andDeletesToken() {
        User user = buildUnverifiedUser();
        EmailVerificationToken vToken = EmailVerificationToken.builder()
                .token("verify-token").user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(emailVerificationTokenRepository.findByToken("verify-token")).thenReturn(Optional.of(vToken));

        authService.verifyEmail("verify-token");

        assertThat(user.getEmailVerified()).isTrue();
        verify(userRepository).save(user);
        verify(emailVerificationTokenRepository).delete(vToken);
    }

    @Test
    void verifyEmail_throws_whenTokenNotFound() {
        when(emailVerificationTokenRepository.findByToken("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail("bad"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("INVALID_VERIFICATION_TOKEN");
    }

    @Test
    void verifyEmail_throws_whenTokenExpired() {
        User user = buildUnverifiedUser();
        EmailVerificationToken vToken = EmailVerificationToken.builder()
                .token("expired").user(user)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(emailVerificationTokenRepository.findByToken("expired")).thenReturn(Optional.of(vToken));

        assertThatThrownBy(() -> authService.verifyEmail("expired"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("VERIFICATION_TOKEN_EXPIRED");
    }

    // ── resendVerification ────────────────────────────────────────────────────

    @Test
    void resendVerification_deletesOldTokenAndSendsNew() {
        User user = buildUnverifiedUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        authService.resendVerification(EMAIL);

        verify(emailVerificationTokenRepository).deleteByUserId(1L);
        verify(emailService).sendVerificationEmail(eq(EMAIL), anyString(), anyString());
    }

    @Test
    void resendVerification_throws_whenAlreadyVerified() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(buildVerifiedUser()));

        assertThatThrownBy(() -> authService.resendVerification(EMAIL))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("EMAIL_ALREADY_VERIFIED");
    }

    @Test
    void resendVerification_throws_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resendVerification("unknown@x.com"))
                .isInstanceOf(RuntimeException.class);
    }
}
