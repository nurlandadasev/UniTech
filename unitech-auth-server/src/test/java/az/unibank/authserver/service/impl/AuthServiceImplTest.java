package az.unibank.authserver.service.impl;

import az.unibank.authserver.dto.request.LoginWithPasswordRequest;
import az.unibank.authserver.dto.request.RefreshTokenRequest;
import az.unibank.authserver.dto.request.RegisterNewUser;
import az.unibank.authserver.mapper.UserMapper;
import az.unibank.authserver.util.PasswordUtils;
import az.unibank.commons.config.Constants;
import az.unibank.commons.dto.Result;
import az.unibank.commons.dto.auth.AuthData;
import az.unibank.commons.dto.auth.RefreshTokenData;
import az.unibank.commons.dto.auth.RoleDTO;
import az.unibank.commons.enums.PasswordValidationResult;
import az.unibank.commons.enums.RoleEnum;
import az.unibank.commons.util.JwtUtils;
import az.unibank.persistence.domains.Role;
import az.unibank.persistence.domains.User;
import az.unibank.persistence.repo.RoleRepository;
import az.unibank.persistence.repo.UserAuthorityRepository;
import az.unibank.persistence.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static az.unibank.commons.enums.ResponseCode.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private UserAuthorityRepository mockUserAuthorityRepository;
    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private PasswordUtils mockPasswordUtils;

    @InjectMocks
    private AuthServiceImpl authServiceImplUnderTest;

    @Test
    void testRegisterUser() {
        // Setup
        final RegisterNewUser registerNewUser = new RegisterNewUser("name", "phone", "pin", "password");

        // Configure UserMapper.mapToEntity(...).
        final User user = User.builder()
                .id(0L)
                .name("name")
                .pin("pin")
                .password("password")
                .role(new Role(RoleEnum.USER.value()))
                .lastLoginDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .isBlocked(0)
                .blockedDate(null)
                .build();

        when(mockUserMapper.mapToEntity(any(RegisterNewUser.class))).thenReturn(user);

        when(mockRoleRepository.findById(RoleEnum.USER.value())).thenReturn(Optional.of(new Role(RoleEnum.USER.value())));

        when(mockUserRepository.findByPin("pin")).thenReturn(null);

        when(mockPasswordUtils.validatePassword(user.getName(), user.getPin(), user.getPassword())).thenReturn(PasswordValidationResult.VALID);

        when(mockPasswordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        // Run the test
        final Object result = authServiceImplUnderTest.registerUser(registerNewUser);

        // Verify the results
//        userRepository.save(user);
        verify(mockUserRepository).save(User.builder()
                .id(0L)
                .name("name")
                .pin("pin")
                .password("password")
                .role(new Role(RoleEnum.USER.value()))
                .lastLoginDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .isBlocked(0)
                .blockedDate(null)
                .build());
    }

    @Test
    void testLoginWithPassword() throws Exception {
        // Setup
        final LoginWithPasswordRequest credentials = LoginWithPasswordRequest.builder()
                .pin("pin")
                .password("password")
                .build();

        // Configure UserRepository.findByPin(...).
        final User user = User.builder()
                .id(0L)
                .name("name")
                .pin("pin")
                .password("password")
                .role(new Role(0))
                .lastLoginDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .isBlocked(0)
                .blockedDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .build();
        when(mockUserRepository.findByPin("pin")).thenReturn(user);

        when(mockPasswordEncoder.matches("password", "password")).thenReturn(true);
        when(mockHttpServletRequest.getRemoteAddr()).thenReturn("result");

        try (MockedStatic<JwtUtils> jwtUtilsMockedStatic = Mockito.mockStatic(JwtUtils.class)) {
            jwtUtilsMockedStatic.when(() -> JwtUtils.generateAccessToken(any()))
                    .thenReturn("123");
            jwtUtilsMockedStatic.when(() -> JwtUtils.generateRefreshToken(any()))
                    .thenReturn("123");

            // Run the test
            final Object result = authServiceImplUnderTest.loginWithPassword(credentials);

            // Verify the results
            then(mockUserRepository).should(atLeastOnce()).save(any());
        }

    }
    @Test
    void testGetUserAuthorities() {
        // Setup
        String token  = "tokentest";
        final Optional<User> user = Optional.of(User.builder()
                .id(1L)
                .name("name")
                .pin("pin")
                .password("password")
                .role(new Role(2))
                .lastLoginDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .isBlocked(0)
                .blockedDate(null)
                .build());

        List<String> authorities = List.of("value");

        when(mockUserRepository.findUserById(user.get().getId())).thenReturn(user);

        when(mockUserAuthorityRepository.findAuthoritiesByUser(user.get().getRole().getId()))
                .thenReturn(authorities);


        try (MockedStatic<JwtUtils> jwtUtilsMockedStatic = Mockito.mockStatic(JwtUtils.class)) {

            jwtUtilsMockedStatic.when(() -> JwtUtils.parseAccessToken(token))
                    .thenReturn(AuthData.builder()
                            .id(1)
                            .selectedRole(RoleDTO.builder().id(2).name("USER").build()).build());

            // Run the test
            final Object result = authServiceImplUnderTest.getUserAuthorities(token);

            // Verify the results
            assertEquals(Result.Builder().response(OK)
                    .add("authorities", authorities)
                    .build(), result);
        }

    }

    @Test
    void testGetUserAuthorities_UserAuthorityRepositoryReturnsNoItems() {
        // Setup
        // Configure UserRepository.findUserById(...).
        String token = "token";
        final Optional<User> user = Optional.of(User.builder()
                .id(1L)
                .name("name")
                .pin("pin")
                .password("password")
                .role(new Role(0))
                .lastLoginDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .isBlocked(0)
                .blockedDate(null)
                .build());
        when(mockUserRepository.findUserById(1L)).thenReturn(user);

        try (MockedStatic<JwtUtils> jwtUtilsMockedStatic = Mockito.mockStatic(JwtUtils.class)) {

            jwtUtilsMockedStatic.when(() -> JwtUtils.parseAccessToken(token))
                    .thenReturn(AuthData.builder()
                            .id(1)
                            .selectedRole(RoleDTO.builder().id(2).name("USER").build()).build());

            // Run the test
            final Object result = authServiceImplUnderTest.getUserAuthorities(token);

            // Verify the results
            assertEquals(Result.Builder().response(OK)
                    .add("authorities", Collections.emptyList())
                    .build(), result);
        }
    }
}
