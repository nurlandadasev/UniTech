package az.unibank.authserver.service.impl;

import az.unibank.authserver.dto.request.LoginWithPasswordRequest;
import az.unibank.authserver.dto.request.RegisterNewUser;
import az.unibank.authserver.mapper.UserMapper;
import az.unibank.authserver.util.PasswordUtils;
import az.unibank.commons.dto.Result;
import az.unibank.commons.enums.PasswordValidationResult;
import az.unibank.commons.enums.RoleEnum;
import az.unibank.persistence.domains.Role;
import az.unibank.persistence.domains.User;
import az.unibank.persistence.repo.RoleRepository;
import az.unibank.persistence.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static az.unibank.commons.enums.ResponseCode.ACCOUNT_ALREADY_REGISTERED;
import static az.unibank.commons.enums.ResponseCode.CREATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTestMy {

    @Mock
    private RegisterNewUser registerNewUser;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordUtils passwordUtils;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testRegisterUser_WhenPinAlreadyExists_ReturnsAccountAlreadyRegisteredResult() {
        RegisterNewUser registerNewUser = RegisterNewUser.builder()
                .password("123NNDSksdkds!@#")
                .name("Nurlan")
                .phone("1234")
                .pin("1234")
                .build();

        Mockito.when(userRepository.findByPin(registerNewUser.getPin())).thenReturn(new User());

        // Act
        Object result = authService.registerUser(registerNewUser);

        // Assert
        // Verify that the expected result is returned
        assertEquals(Result.Builder().response(ACCOUNT_ALREADY_REGISTERED)
                .add("errorMessage", String.format("User with this pin %s already exists", registerNewUser.getPin()))
                .build(), result);
    }

    @Test
    public void testRegisterUser_WhenValidRegistration_ReturnsCreatedResult() {
        RegisterNewUser registerNewUser = RegisterNewUser.builder()
                .password("123NNDSksdkds!@#")
                .name("Nurlan")
                .phone("1234")
                .pin("1234")
                .build();

        when(userMapper.mapToEntity(registerNewUser)).thenReturn(new User());
        when(userRepository.findByPin(registerNewUser.getPin())).thenReturn(null);
        when(roleRepository.findById(RoleEnum.USER.value())).thenReturn(Optional.of(new Role()));
        when(passwordUtils.validatePassword(anyString(), eq(registerNewUser.getPin()), eq(registerNewUser.getPassword()))).thenReturn(PasswordValidationResult.VALID);

        Object result = authService.registerUser(registerNewUser);

        assertEquals(Result.Builder().response(CREATED).build(), result);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginWithPassword_WhenValidCredentials_ReturnsLoginTokens() {
        // Arrange
        LoginWithPasswordRequest loginWithPasswordRequest = LoginWithPasswordRequest.builder()
                .password("Password123!@#")
                .pin("1234")
                .build();

        User user = new User();
        user.setIsBlocked(1);
        user.setLastLoginDate(null);

        when(userRepository.findByPin(loginWithPasswordRequest.getPin())).thenReturn(user);
//        when(JwtUtils.generateAccessToken(new AuthData())).thenReturn("RandomToken");

        // Act
        Object result = authService.loginWithPassword(loginWithPasswordRequest);

        // Verify that the updated user is saved in the userRepository
//        verify(userRepository, times(1)).save(user);
    }

    @Test
    void refreshToken() {
    }

    @Test
    void getUserAuthorities() {
    }

    @Test
    void buildAuthDataFromUser() {
    }

    @Test
    void buildRefreshTokenDataFromUser() {
    }
}