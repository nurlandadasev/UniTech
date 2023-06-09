package az.unibank.authserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Config {

    public static final Map<Integer, List<String>> ROLE_AUTHORITIES = new ConcurrentHashMap<>();
}