package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.EmailUpdateResponseDto;
import com.fintrack.fintrackbackend.dto.UserProfileResponseDto;
import com.fintrack.fintrackbackend.dto.UserRequestDto;
import com.fintrack.fintrackbackend.dto.UserResponseDto;
import com.fintrack.fintrackbackend.entity.AuthProvider;
import com.fintrack.fintrackbackend.entity.RefreshToken;
import com.fintrack.fintrackbackend.entity.User;
import com.fintrack.fintrackbackend.exception.BusinessException;
import com.fintrack.fintrackbackend.exception.ErrorCode;
import com.fintrack.fintrackbackend.mapper.UserMapper;
import com.fintrack.fintrackbackend.repository.RefreshTokenRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper mapper;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public UserResponseDto registerUser(UserRequestDto dto) {
        log.info("Kayıt isteği: email={}", dto.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Kayıt başarısız, email zaten mevcut: email={}", dto.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = mapper.mapToEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("Kayıt başarılı: email={}", savedUser.getEmail());

        String token = jwtUtil.generateToken(savedUser.getEmail());
        RefreshToken refreshToken = createRefreshToken(savedUser);
        return mapper.mapToDto(savedUser, token, refreshToken.getToken());
    }

    public UserResponseDto loginUser(String email, String password) {
        log.info("Giriş isteği: email={}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Giriş başarısız, kullanıcı bulunamadı: email={}", email);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Giriş başarısız, hatalı şifre: email={}", email);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        log.info("Giriş başarılı: email={}", email);

        String token = jwtUtil.generateToken(user.getEmail());
        RefreshToken refreshToken = createRefreshToken(user);
        return mapper.mapToDto(user, token, refreshToken.getToken());
    }

    public UserResponseDto loginWithGoogle(String idTokenString) {
        log.info("Google ile giriş isteği alındı");
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException(ErrorCode.GOOGLE_AUTH_FAILED);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .firstName(firstName != null ? firstName : "")
                        .lastName(lastName != null ? lastName : "")
                        .email(email)
                        .authProvider(AuthProvider.GOOGLE)
                        .build();
                return userRepository.save(newUser);
            });

            log.info("Google ile giriş başarılı: email={}", email);

            String token = jwtUtil.generateToken(user.getEmail());
            RefreshToken refreshToken = createRefreshToken(user);
            return mapper.mapToDto(user, token, refreshToken.getToken());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google kimlik doğrulama hatası: {}", e.getMessage());
            throw new BusinessException(ErrorCode.GOOGLE_AUTH_FAILED);
        }
    }

    @Transactional
    public UserResponseDto refreshToken(String token) {
        log.info("Token yenileme isteği alındı");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);

        String accessToken = jwtUtil.generateToken(user.getEmail());
        RefreshToken newRefreshToken = createRefreshToken(user);

        log.info("Token yenileme başarılı: email={}", user.getEmail());

        return mapper.mapToDto(user, accessToken, newRefreshToken.getToken());
    }

    public UserProfileResponseDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ensurePasswordChangedAt(user);
        return mapper.mapToProfileDto(user);
    }

    @Transactional
    public void logout(String email) {
        log.info("Çıkış isteği: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUserId(user.getId());

        log.info("Çıkış başarılı: email={}", email);
    }

    @Transactional
    public UserProfileResponseDto updateName(String email, String firstName, String lastName) {
        log.info("Ad soyad güncelleme isteği: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        ensurePasswordChangedAt(user);
        userRepository.save(user);

        log.info("Ad soyad güncelleme başarılı: email={}", email);

        return mapper.mapToProfileDto(user);
    }

    @Transactional
    public EmailUpdateResponseDto updateEmail(String email, String newEmail) {
        log.info("E-posta güncelleme isteği: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!newEmail.equals(user.getEmail()) && userRepository.findByEmail(newEmail).isPresent()) {
            log.warn("E-posta güncelleme başarısız, email zaten mevcut: email={}", newEmail);
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        user.setEmail(newEmail);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        RefreshToken refreshToken = createRefreshToken(user);

        log.info("E-posta güncelleme başarılı: email={}", newEmail);

        return EmailUpdateResponseDto.builder()
                .email(user.getEmail())
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public UserProfileResponseDto updatePassword(String email, String currentPassword, String newPassword) {
        log.info("Şifre güncelleme isteği: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Şifre güncelleme başarısız, mevcut şifre hatalı: email={}", email);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Şifre güncelleme başarılı: email={}", email);

        return mapper.mapToProfileDto(user);
    }

    private void ensurePasswordChangedAt(User user) {
        if (user.getPasswordChangedAt() == null) {
            user.setPasswordChangedAt(user.getCreatedAt());
            userRepository.save(user);
        }
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}
