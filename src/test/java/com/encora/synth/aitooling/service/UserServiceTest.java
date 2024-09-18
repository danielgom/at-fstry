package com.encora.synth.aitooling.service;

import com.encora.synth.aitooling.dto.UserGetResponse;
import com.encora.synth.aitooling.dto.exception.UserException;
import com.encora.synth.aitooling.model.User;
import com.encora.synth.aitooling.repository.JWTBlackListRepository;
import com.encora.synth.aitooling.repository.UserRepository;
import com.encora.synth.aitooling.security.JWTProvider;
import com.encora.synth.aitooling.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private JWTProvider jwtProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTBlackListRepository jwtBlackListRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void validEmail_shouldGetUser() {
        User user = User.builder()
                .id("1234abcd")
                .name("Daniel")
                .password("$2a$12$yeEmIDYFPzlSLakWFckaBu08E43Pxp.eQ6Q5.t65hqhALoJQ2qBxi")
                .lastName("Test")
                .email("testing_service@outlook.com")
                .createdAt(LocalDateTime.of(2024, 3, 2, 12, 0, 0))
                .build();

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));

        UserGetResponse response = userServiceImpl.getByEmail("testing_service@outlook.com");
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("1234abcd");
        assertThat(response.getName()).isEqualTo("Daniel");
        assertThat(response.getLastName()).isEqualTo("Test");
        assertThat(response.getEmail()).isEqualTo("testing_service@outlook.com");
        assertThat(response.getCreatedAt()).isEqualTo(
                LocalDateTime.of(2024, 3, 2, 12, 0, 0));
    }

    @Test
    void notFoundEmail_shouldThrowException() {
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        UserException userException = assertThrows(UserException.class,
                () -> userServiceImpl.getByEmail("testing_service@outlook.com"));

        assertThat(userException).isNotNull();
        assertThat(userException.getMessage()).isEqualTo("user not found");
        assertThat(userException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserIDWithNoSecurityContext_shouldThrowException() {
        UserException userException = assertThrows(UserException.class,
                () -> userServiceImpl.getUserID());

        assertThat(userException).isNotNull();
        assertThat(userException.getMessage()).isEqualTo("unable to find authenticated user");
        assertThat(userException.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void logoutWithInvalidJWT_shouldNotSaveToBlacklist() {
        userServiceImpl.logout("invalid-jwt");
        Mockito.verify(jwtBlackListRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void logoutWithInvalidInvalidJWTSignature_shouldNotSaveToBlacklist() {
        userServiceImpl.logout("Bearer adfkahjdflahsdff");
        Mockito.verify(jwtBlackListRepository, Mockito.never()).save(Mockito.any());
    }
}
