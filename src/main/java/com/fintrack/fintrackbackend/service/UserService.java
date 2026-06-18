package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.UserRequestDto;
import com.fintrack.fintrackbackend.dto.UserResponseDto;
import com.fintrack.fintrackbackend.entity.AuthProvider;
import com.fintrack.fintrackbackend.entity.User;
import com.fintrack.fintrackbackend.exception.BusinessException;
import com.fintrack.fintrackbackend.exception.ErrorCode;
import com.fintrack.fintrackbackend.mapper.UserMapper;
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
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper mapper;

    @Value("${google.client-id}")
    private String googleClientId;

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
        return mapper.mapToDto(savedUser, token);
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
        return mapper.mapToDto(user, token);
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
            return mapper.mapToDto(user, token);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google kimlik doğrulama hatası: {}", e.getMessage());
            throw new BusinessException(ErrorCode.GOOGLE_AUTH_FAILED);
        }
    }
}