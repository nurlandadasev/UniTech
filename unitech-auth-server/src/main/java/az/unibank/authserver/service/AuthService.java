package az.unibank.authserver.service;


import az.unibank.authserver.dto.request.LoginWithPasswordRequest;
import az.unibank.authserver.dto.request.RefreshTokenRequest;
import az.unibank.authserver.dto.request.RegisterNewUser;

public interface AuthService {

    Object registerUser (RegisterNewUser registerNewUser);
    Object loginWithPassword(LoginWithPasswordRequest request);

    Object refreshToken(RefreshTokenRequest request);

    Object getUserAuthorities(String token);
}