package az.unibank.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Config {

    public static final Map<Integer, List<String>> ROLE_AUTHORITIES = new ConcurrentHashMap<>();


}