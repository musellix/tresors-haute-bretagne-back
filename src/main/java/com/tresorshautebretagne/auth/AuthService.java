package com.tresorshautebretagne.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
import com.tresorshautebretagne.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final MapperService mapperService;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${app.google.client-id}")
    private String googleClientId;

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setEmailVerified(false);
        User saved = userRepository.save(user);

        sendVerificationEmail(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new RuntimeException("EMAIL_NOT_VERIFIED");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse loginWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = verifyGoogleToken(idToken);

        String email = payload.getEmail().toLowerCase();
        String googleId = payload.getSubject();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name != null ? name : email);
            newUser.setAvatarUrl(picture);
            newUser.setGoogleId(googleId);
            newUser.setEmailVerified(true);
            return newUser;
        });

        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
        }
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            user.setEmailVerified(true);
        }
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("INVALID_REFRESH_TOKEN"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())
                || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("REFRESH_TOKEN_EXPIRED");
        }

        // Rotation : on révoque l'ancien et on en émet un nouveau
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(refreshToken.getUser());
    }

    @Transactional
    public void logout(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("INVALID_VERIFICATION_TOKEN"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("VERIFICATION_TOKEN_EXPIRED");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new RuntimeException("EMAIL_ALREADY_VERIFIED");
        }

        emailVerificationTokenRepository.deleteByUserId(user.getId());
        sendVerificationEmail(user);
    }

    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        emailVerificationTokenRepository.save(
                EmailVerificationToken.builder()
                        .token(token)
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusHours(24))
                        .build()
        );
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user.getId());
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(refreshTokenValue)
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                        .build()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .user(mapperService.userToDTO(user))
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new RuntimeException("Token Google invalide ou expiré");
            }
            return token.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erreur lors de la vérification Google : " + e.getMessage());
        }
    }
}
