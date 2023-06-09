package az.unibank.apigateway.security;

import az.unibank.commons.config.Constants;
import az.unibank.commons.util.JwtUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @PostConstruct
    private void setJwtSecretKey() {
        JwtUtils.setSecretKey(Constants.JWT_SECRET_KEY);
    }

}
