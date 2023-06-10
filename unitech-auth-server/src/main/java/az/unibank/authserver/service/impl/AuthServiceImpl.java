package az.unibank.authserver.service.impl;


import az.unibank.authserver.config.Config;
import az.unibank.authserver.dto.request.LoginWithPasswordRequest;
import az.unibank.authserver.dto.request.RefreshTokenRequest;
import az.unibank.authserver.dto.request.RegisterNewUser;
import az.unibank.authserver.mapper.UserMapper;

import az.unibank.authserver.service.AuthService;
import az.unibank.authserver.util.PasswordUtils;
import az.unibank.commons.config.Constants;
import az.unibank.commons.dto.Result;
import az.unibank.commons.dto.auth.AuthData;
import az.unibank.commons.dto.auth.RefreshTokenData;
import az.unibank.commons.dto.auth.RoleDTO;
import az.unibank.commons.enums.PasswordValidationResult;
import az.unibank.commons.enums.RoleEnum;
import az.unibank.commons.exception.UnauthorizedException;
import az.unibank.commons.util.JwtUtils;
import az.unibank.persistence.domains.Role;
import az.unibank.persistence.domains.User;
import az.unibank.persistence.repo.RoleRepository;
import az.unibank.persistence.repo.UserAuthorityRepository;
import az.unibank.persistence.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static az.unibank.commons.enums.ResponseCode.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthorityRepository userAuthorityRepository;
    private final HttpServletRequest httpServletRequest;
    private final UserMapper userMapper;
    private final PasswordUtils passwordUtils;

    @Transactional
    @Override
    public Object registerUser(RegisterNewUser registerNewUser) {
        User user = userMapper.mapToEntity(registerNewUser);
        Optional<Role> userRole = roleRepository.findById(RoleEnum.USER.value());

        String pinAfterTrim = registerNewUser.getPin().trim();
        User byPin = userRepository.findByPin(pinAfterTrim);

        if (byPin != null)
            return Result.Builder().response(ACCOUNT_ALREADY_REGISTERED)
                    .add("errorMessage", String.format("User with this pin %s already exists", pinAfterTrim))
                    .build();


        if (userRole.isEmpty())
            return Result.Builder().response(SERVER_ERROR)
                    .add("errorMessage", "User role not found")
                    .build();

        PasswordValidationResult validationResult = passwordUtils.validatePassword(
                registerNewUser.getName(),
                registerNewUser.getPin(),
                registerNewUser.getPassword()
        );

        if (validationResult != PasswordValidationResult.VALID) {
            log.error("<register> password does not meet requirements for user: {}, result: {}",
                    registerNewUser.getPin(),
                    validationResult);

            return Result.Builder().response(INVALID_VALUE)
                    .add("passwordResult", validationResult)
                    .build();
        }


        user.setRole(userRole.get());
        user.setIsBlocked(0);
        user.setPassword(passwordEncoder.encode(registerNewUser.getPassword()));

        userRepository.save(user);

        return Result.Builder().response(CREATED)
                .build();
    }

    @Override
    public Object loginWithPassword(LoginWithPasswordRequest credentials) {
        User user = userRepository.findByPin(credentials.getPin());

        if (!checkLoginWithPinAndPassword(user, credentials.getPassword())) {

            if (nonNull(user)) {

                if (user.getIsBlocked() == 1) {
                    return userBlocked(user);
                }

            }

            return Result.Builder().response(UNAUTHORIZED)
                    .add(Constants.ERROR, "Incorrect pin or password")
                    .build();
        }

        if (user.getIsBlocked() == 1) {
            return userBlocked(user);
        }

        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        return generateLoginTokens(user);
    }

    private Object userBlocked(User user) {

        log.error("<login> user temporary blocked: {}, ip: {}", user.getPin(), httpServletRequest.getRemoteAddr());
        long duration = Duration.between(user.getBlockedDate(), LocalDateTime.now()).toMinutes();

        return Result.Builder().response(ACCOUNT_BLOCKED)
                .add("duration", Math.max(duration, 1))
                .add("unit", "minutes")
                .build();
    }

    private boolean checkLoginWithPinAndPassword(User user, String password) {
        try {
            if (isNull(user)
                    || isNull(user.getPassword())
                    || user.getPassword().isBlank()
                    || !passwordEncoder.matches(password, user.getPassword())) {
                return false;
            }

            log.info("<checkLoginWithPinAndPassword> success for pin: {}, ip: {}",
                    user.getPin(), httpServletRequest.getRemoteAddr());
            return true;
        } catch (AuthenticationException e) {
            log.error("<checkLoginWithPinAndPassword> invalid pin {{}} or password. Ip: {}, Error: {}",
                    user.getPin(), httpServletRequest.getRemoteAddr(), e.getMessage());
            return false;
        }
    }

    @Override
    public Object refreshToken(RefreshTokenRequest params) {

        try {
            RefreshTokenData refreshTokenData = JwtUtils.parseRefreshToken(params.getRefreshToken());
            User user = userRepository.findById(refreshTokenData.getUserId()).orElseThrow(NullPointerException::new);
            log.info("<refreshToken> success for pin: {}, ip: {}", user.getPin(), httpServletRequest.getRemoteAddr());
            return generateLoginTokens(user);
        } catch (UnauthorizedException | NullPointerException e) {
            log.error("<refreshToken> invalid refresh token {}, ip: {}", params.getRefreshToken(), httpServletRequest.getRemoteAddr());
            return Result.Builder().response(UNAUTHORIZED)
                    .build();
        }
        catch (Exception e) {
            log.error("<refreshToken> Error in refresh token {}, ip: {}", params.getRefreshToken(), httpServletRequest.getRemoteAddr(), e);
            return Result.Builder().response(SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public Object getUserAuthorities(String token) {

        try {
            String jwt = token.replaceFirst("Bearer ", "");
            AuthData authData = JwtUtils.parseAccessToken(jwt);

            List<String> authorities = Config.ROLE_AUTHORITIES.get(authData.getSelectedRole().getId());

            if (isNull(authorities)) {
                User user = userRepository.findUserById(authData.getId()).orElseThrow();
                authorities = userAuthorityRepository.findAuthoritiesByUser(user.getRole().getId());

                Config.ROLE_AUTHORITIES.put(authData.getSelectedRole().getId(), authorities);
            }

            return Result.Builder().response(OK)
                    .add("authorities", authorities)
                    .build();

        } catch (Exception e) {
            log.error("<getUserAuthorities> Error ", e);
            return Result.Builder().response(UNAUTHORIZED).build();
        }
    }

    private Object generateLoginTokens(User user) {

        final String accessToken = JwtUtils.generateAccessToken(buildAuthDataFromUser(user));
        final String refreshToken = JwtUtils.generateRefreshToken(buildRefreshTokenDataFromUser(user));

        return Result.Builder().response(OK)
                .add("accessToken", accessToken)
                .add("refreshToken", refreshToken)
                .build();
    }

    public AuthData buildAuthDataFromUser(User user) {

        AuthData authData = new AuthData();
        authData.setId(user.getId());
        authData.setName(user.getName());
        authData.setPin(user.getPin());

        authData.setSelectedRole(RoleDTO.builder()
                .id(user.getRole().getId())
                .name(user.getRole().getName())
                .build());

        return authData;
    }

    public RefreshTokenData buildRefreshTokenDataFromUser(User user) {
        RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setUserId(user.getId());
        return refreshTokenData;
    }
}