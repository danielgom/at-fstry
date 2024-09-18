package com.encora.synth.aitooling.service.impl;

import com.encora.synth.aitooling.dto.*;
import com.encora.synth.aitooling.dto.exception.UserException;
import com.encora.synth.aitooling.mapper.UserMapper;
import com.encora.synth.aitooling.model.JWTBlackList;
import com.encora.synth.aitooling.model.RefreshToken;
import com.encora.synth.aitooling.model.User;
import com.encora.synth.aitooling.repository.JWTBlackListRepository;
import com.encora.synth.aitooling.repository.RefreshTokenRepository;
import com.encora.synth.aitooling.repository.UserRepository;
import com.encora.synth.aitooling.security.JWTHolder;
import com.encora.synth.aitooling.security.JWTProvider;
import com.encora.synth.aitooling.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JWTProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JWTBlackListRepository jwtBlackListRepository;

    @Value("${RefreshToken.expire-duration-days}")
    private int refreshTokenExpireDurationDays;

    @Override
    public UserCreateResponse create(UserCreateRequest request) {
        User user = UserMapper.MAPPER.toUser(request);

        String password = user.getPassword();
        if (!isValidPassword(password)) {
            throw new UserException("password must be at least 8 characters long, contain one uppercase letter," +
                    " one lowercase letter, one number, and one special character.", HttpStatus.BAD_REQUEST);
        }
        if (!isValidEmail(user.getEmail())) {
            throw new UserException("invalid email", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DuplicateKeyException ex) {
            throw new UserException("user with this email already exists", HttpStatus.CONFLICT);
        }

        return UserCreateResponse.builder()
                .name(savedUser.getName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public UserGetResponse getByEmail(String email) {
        return UserMapper.MAPPER.toUserGetResponse(getUser(email));
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = getUser(request.getEmail());

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            JWTHolder jwtHolder = jwtProvider.generateToken(user.getId(), user.getEmail());

            String refreshToken = UUID.randomUUID().toString();
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .userID(user.getId())
                    .expiresAt(LocalDateTime.now().plusDays(refreshTokenExpireDurationDays))
                    .build();

            refreshTokenRepository.save(refreshTokenEntity);

            return UserLoginResponse.builder()
                    .token(jwtHolder.getJWTToken())
                    .refreshToken(refreshToken)
                    .expiresAt(jwtHolder.getExpiresAt())
                    .userGetResponse(UserMapper.MAPPER.toUserGetResponse(user))
                    .build();
        }

        throw new UserException("invalid password please try again", HttpStatus.BAD_REQUEST);
    }

    @Override
    public UserRefreshResponse refreshToken(HttpServletRequest request) {
        UserGetResponse user = (UserGetResponse) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String refreshToken = getRefreshTokenFromCookie(request);
        RefreshToken rt = refreshTokenRepository.findByUserIDAndToken(user.getId(), refreshToken).orElseThrow(()
                -> new UserException("refresh token not found in db", HttpStatus.NOT_FOUND));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UserException("refresh token is expired", HttpStatus.BAD_REQUEST);
        }

        JWTHolder jwtHolder = jwtProvider.generateToken(user.getId(), user.getEmail());
        return UserRefreshResponse.builder()
                .token(jwtHolder.getJWTToken())
                .expiresAt(jwtHolder.getExpiresAt())
                .build();
    }

    @Override
    public void logout(String bearerToken) {
        String jwt = getJwtFromRequest(bearerToken);
        if (jwtProvider.isValidToken(jwt)) {
            jwtBlackListRepository.save(JWTBlackList.builder().expiredToken(jwt).build());
        }
    }

    public String getUserID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserGetResponse user) {
            return user.getId();
        }

        throw new UserException("unable to find authenticated user", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        throw new UserException("refreshToken cookie not found", HttpStatus.BAD_REQUEST);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserException("user not found", HttpStatus.NOT_FOUND));
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    }

    private boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private String getJwtFromRequest(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replace("Bearer ", "");
        }
        return "";
    }
}
