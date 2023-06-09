package az.unibank.authserver.service.impl;


import az.unibank.authserver.config.Config;
import az.unibank.authserver.dto.request.LoginWithPasswordRequest;
import az.unibank.authserver.dto.request.RefreshTokenRequest;
import az.unibank.authserver.models.User;
import az.unibank.authserver.repo.UserAuthorityRepository;
import az.unibank.authserver.repo.UserRepository;
import az.unibank.authserver.service.AuthService;
import az.unibank.commons.config.Constants;
import az.unibank.commons.domains.Result;
import az.unibank.commons.dto.auth.AuthData;
import az.unibank.commons.dto.auth.RefreshTokenData;
import az.unibank.commons.dto.auth.RoleDTO;
import az.unibank.commons.dto.auth.StatusDTO;
import az.unibank.commons.exception.UnauthorizedException;
import az.unibank.commons.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static az.unibank.commons.enums.ResponseCode.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthorityRepository userAuthorityRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public Object loginWithPassword(LoginWithPasswordRequest credentials) {
        User user = userRepository.findByUsername(credentials.getUsername());

        if (!checkLoginWithUsernameAndPassword(user, credentials.getPassword())) {

            if (nonNull(user)) {

                if (user.getIsBlocked() == 1) {
                    return userBlocked(user);
                }

            }

            return Result.Builder().response(UNAUTHORIZED)
                    .add(Constants.ERROR, "Incorrect username or password")
                    .build();
        }

        if (user.getIsBlocked() == 1) {
            return userBlocked(user);
        }

        return generateLoginTokens(user, false);
    }

    private Object userBlocked(User user) {

        log.error("<login> user temporary blocked: {}, ip: {}", user.getUsername(), httpServletRequest.getRemoteAddr());
        long duration = Duration.between(user.getBlockedDate(), LocalDateTime.now()).toMinutes();

        return Result.Builder().response(ACCOUNT_BLOCKED)
                .add("duration", Math.max(duration, 1))
                .add("unit", "minutes")
                .build();
    }

    private boolean checkLoginWithUsernameAndPassword(User user, String password) {
        try {
            if (isNull(user)
                    || isNull(user.getPassword())
                    || user.getPassword().isBlank()
                    || !passwordEncoder.matches(password, user.getPassword())
                    || user.getStatus() < 0) {
                return false;
            }

            log.info("<checkLoginWithUsernameAndPassword> success for username: {}, ip: {}",
                    user.getUsername(), httpServletRequest.getRemoteAddr());
            return true;
        } catch (AuthenticationException e) {
            log.error("<checkLoginWithUsernameAndPassword> invalid username {{}} or password. Ip: {}, Error: {}",
                    user.getUsername(), httpServletRequest.getRemoteAddr(), e.getMessage());
            return false;
        }
    }

    @Override
    public Object refreshToken(RefreshTokenRequest params) {

        try {
            RefreshTokenData refreshTokenData = JwtUtils.parseRefreshToken(params.getRefreshToken());
            User user = userRepository.findById(refreshTokenData.getUserId()).orElseThrow(NullPointerException::new);
            log.info("<refreshToken> success for username: {}, ip: {}", user.getUsername(), httpServletRequest.getRemoteAddr());
            return generateLoginTokens(user, false);
        } catch (UnauthorizedException e) {
            log.error("<refreshToken> invalid refresh token {}, ip: {}", params.getRefreshToken(), httpServletRequest.getRemoteAddr());
            return Result.Builder().response(UNAUTHORIZED)
                    .build();
        } catch (Exception e) {
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

    private Object generateLoginTokens(User user, boolean loginWithOtp) {

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
        authData.setUsername(user.getUsername());
        StatusDTO status = new StatusDTO();
        status.setId(user.getStatus());

        if (status.getId() == 1)
            status.setName("Active");
        else if (status.getId() == 0) {
            status.setName("Inactive");
        }

        authData.setStatus(status);

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