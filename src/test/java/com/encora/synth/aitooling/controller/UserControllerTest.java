package com.encora.synth.aitooling.controller;

import com.encora.synth.aitooling.dto.*;
import com.encora.synth.aitooling.dto.Error;
import com.encora.synth.aitooling.utils.MongoDBContainerTestExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ExtendWith(MongoDBContainerTestExtension.class)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String TEST_HOST = "http://localhost:";

    private static final int TEST_EXP_HOURS = 1;

    @Nested
    class CreateUser {
        @Test
        void whenValidRequest_shouldReturnCreatedUser() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("dga_test@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse createResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);

            Assertions.assertThat(createResponse).isNotNull();
            Assertions.assertThat(createResponse.getName()).isEqualTo("Daniel");
            Assertions.assertThat(createResponse.getLastName()).isEqualTo("Test");
            Assertions.assertThat(createResponse.getEmail()).isEqualTo("dga_test@outlook.com");
        }

        @Test
        void whenInvalidPassword_shouldThrowException() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("dga_test@outlook.com")
                    .password("notvalidpass")
                    .build();

            Error errException = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, Error.class);

            Assertions.assertThat(errException).isNotNull();
            Assertions.assertThat(errException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            Assertions.assertThat(errException.getError()).isEqualTo(HttpStatus.BAD_REQUEST.name());
            Assertions.assertThat(errException.getReason()).isEqualTo("password must be at least 8 characters long," +
                    " contain one uppercase letter, one lowercase letter, one number, and one special character.");
            Assertions.assertThat(errException.getTimestamp()).isCloseTo(LocalDateTime.now(),
                    Assertions.within(5, ChronoUnit.SECONDS));
            Assertions.assertThat(errException.getPath()).isEqualTo("/api/auth/signup");
        }

        @Test
        void whenInvalidEmail_shouldThrowException() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("dga_test@")
                    .password("ValidPass1234@")
                    .build();

            Error errException = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, Error.class);

            Assertions.assertThat(errException).isNotNull();
            Assertions.assertThat(errException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            Assertions.assertThat(errException.getError()).isEqualTo(HttpStatus.BAD_REQUEST.name());
            Assertions.assertThat(errException.getReason()).isEqualTo("invalid email");
            Assertions.assertThat(errException.getTimestamp()).isCloseTo(LocalDateTime.now(),
                    Assertions.within(5, ChronoUnit.SECONDS));
            Assertions.assertThat(errException.getPath()).isEqualTo("/api/auth/signup");
        }

        @Test
        void whenDuplicateEmail_shouldThrowException() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("dga_duplicate@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);

            Assertions.assertThat(userCreateResponse).isNotNull();
            Error errException = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, Error.class);

            Assertions.assertThat(errException).isNotNull();
            Assertions.assertThat(errException.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
            Assertions.assertThat(errException.getError()).isEqualTo(HttpStatus.CONFLICT.name());
            Assertions.assertThat(errException.getReason()).isEqualTo("user with this email already exists");
            Assertions.assertThat(errException.getTimestamp()).isCloseTo(LocalDateTime.now(),
                    Assertions.within(5, ChronoUnit.SECONDS));
            Assertions.assertThat(errException.getPath()).isEqualTo("/api/auth/signup");
        }
    }

    @Nested
    class LoginUser {
        @Test
        void whenValidRequest_shouldReturn_JWT_RT_userinfo() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("login_user@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);
            Assertions.assertThat(userCreateResponse).isNotNull();

            UserLoginRequest loginRequestTest = UserLoginRequest.builder()
                    .email("login_user@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserLoginResponse loginResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/login",
                    loginRequestTest, UserLoginResponse.class);

            Assertions.assertThat(loginResponse).isNotNull();
            Assertions.assertThat(loginResponse.getToken()).isNotBlank();
            Assertions.assertThat(loginResponse.getRefreshToken()).isNotBlank();
            Assertions.assertThat(loginResponse.getExpiresAt()).isCloseTo(LocalDateTime.now(), Assertions.within(TEST_EXP_HOURS, ChronoUnit.HOURS));
            Assertions.assertThat(loginResponse.getUserGetResponse()).isNotNull();
            Assertions.assertThat(loginResponse.getUserGetResponse().getId()).isNotBlank();
            Assertions.assertThat(loginResponse.getUserGetResponse().getName()).isEqualTo("Daniel");
            Assertions.assertThat(loginResponse.getUserGetResponse().getLastName()).isEqualTo("Test");
            Assertions.assertThat(loginResponse.getUserGetResponse().getEmail()).isEqualTo("login_user@outlook.com");
            Assertions.assertThat(loginResponse.getUserGetResponse().getCreatedAt())
                    .isCloseTo(LocalDateTime.now(), Assertions.within(5, ChronoUnit.SECONDS));
        }

        @Test
        void whenInvalidPassword_shouldReturnException() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("login_user_invalid@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);
            Assertions.assertThat(userCreateResponse).isNotNull();

            createRequestTest.setPassword("aaaabbbcccdddd");
            Error errException = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/login",
                    createRequestTest, Error.class);

            Assertions.assertThat(errException).isNotNull();
            Assertions.assertThat(errException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            Assertions.assertThat(errException.getError()).isEqualTo(HttpStatus.BAD_REQUEST.name());
            Assertions.assertThat(errException.getReason()).isEqualTo("invalid password please try again");
            Assertions.assertThat(errException.getTimestamp()).isCloseTo(LocalDateTime.now(),
                    Assertions.within(5, ChronoUnit.SECONDS));
            Assertions.assertThat(errException.getPath()).isEqualTo("/api/auth/login");
        }
    }

    @Nested
    class RefreshTokenUser {
        @Test
        void whenValidJWT_shouldReturn_JWT_RT() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("refresh_token@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);
            Assertions.assertThat(userCreateResponse).isNotNull();

            UserLoginRequest loginRequestTest = UserLoginRequest.builder()
                    .email("refresh_token@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserLoginResponse loginResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/login",
                    loginRequestTest, UserLoginResponse.class);

            Assertions.assertThat(loginResponse).isNotNull();
            Assertions.assertThat(loginResponse.getToken()).isNotBlank();
            Assertions.assertThat(loginResponse.getRefreshToken()).isNotBlank();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", loginResponse.getToken()));
            headers.set("Cookie", "refreshToken=" + loginResponse.getRefreshToken());
            HttpEntity<Void> request = new HttpEntity<>(null, headers);
            UserRefreshResponse userRefreshResponse = testRestTemplate
                    .postForObject(TEST_HOST + port + "/api/auth/refresh", request, UserRefreshResponse.class);

            Assertions.assertThat(userRefreshResponse).isNotNull();
            Assertions.assertThat(userRefreshResponse.getToken()).isNotBlank();
            Assertions.assertThat(userRefreshResponse.getExpiresAt()).isCloseTo(LocalDateTime.now(),
                    Assertions.within(TEST_EXP_HOURS, ChronoUnit.HOURS));
        }

        @Test
        void whenBadRefreshToken_shouldThrowException() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("refresh_token_bad_token@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);
            Assertions.assertThat(userCreateResponse).isNotNull();

            UserLoginRequest loginRequestTest = UserLoginRequest.builder()
                    .email("refresh_token_bad_token@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserLoginResponse loginResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/login",
                    loginRequestTest, UserLoginResponse.class);

            Assertions.assertThat(loginResponse).isNotNull();
            Assertions.assertThat(loginResponse.getToken()).isNotBlank();
            Assertions.assertThat(loginResponse.getRefreshToken()).isNotBlank();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", loginResponse.getToken()));
            headers.set("Cookie", "refreshToken=" + "bad-token-over-here");
            HttpEntity<Void> request = new HttpEntity<>(null, headers);
            Error errException = testRestTemplate
                    .postForObject(TEST_HOST + port + "/api/auth/refresh", request, Error.class);

            Assertions.assertThat(errException).isNotNull();
            Assertions.assertThat(errException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            Assertions.assertThat(errException.getError()).isEqualTo(HttpStatus.NOT_FOUND.name());
            Assertions.assertThat(errException.getReason()).isEqualTo("refresh token not found in db");
        }
    }

    @Nested
    class LogoutUser {
        @Test
        void whenValidJWT_shouldSendJWTToBlacklist() {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("logout_user@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest, UserCreateResponse.class);
            Assertions.assertThat(userCreateResponse).isNotNull();

            UserLoginRequest loginRequestTest = UserLoginRequest.builder()
                    .email("logout_user@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserLoginResponse loginResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/login",
                    loginRequestTest, UserLoginResponse.class);

            Assertions.assertThat(loginResponse).isNotNull();
            Assertions.assertThat(loginResponse.getToken()).isNotBlank();
            Assertions.assertThat(loginResponse.getRefreshToken()).isNotBlank();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", loginResponse.getToken()));
            HttpEntity<Void> request = new HttpEntity<>(null, headers);

            testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/logout", request, Void.class);
        }
    }
}

