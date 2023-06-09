package az.unibank.authserver.service;


import az.unibank.authserver.dto.request.LoginWithPasswordRequest;
import az.unibank.authserver.dto.request.RefreshTokenRequest;

public interface AuthService {

    Object loginWithPassword(LoginWithPasswordRequest request);

    Object refreshToken(RefreshTokenRequest request);

    Object getUserAuthorities(String token);
}