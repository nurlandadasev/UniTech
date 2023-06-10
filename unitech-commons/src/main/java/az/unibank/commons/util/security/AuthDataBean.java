package az.unibank.commons.util.security;

import az.unibank.commons.dto.auth.AuthData;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthDataBean {

    private AuthData user;
}