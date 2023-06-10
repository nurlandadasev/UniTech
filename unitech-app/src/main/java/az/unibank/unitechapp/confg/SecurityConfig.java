package az.unibank.unitechapp.confg;


import az.unibank.commons.config.Constants;
import az.unibank.commons.util.JwtUtils;
import az.unibank.unitechapp.filter.JwtRequestFilter;
import az.unibank.unitechapp.utils.CustomPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import jakarta.annotation.PostConstruct;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("SHA-1", new CustomPasswordEncoder());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests(authorize -> {
                            try {
                                authorize
                                        .requestMatchers("/docs", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/actuator/**").permitAll()
                                        .requestMatchers("/public/**").permitAll()
                                        .requestMatchers("/accounts/**").permitAll()
                                        .anyRequest().authenticated()
                                        .and()
                                        .cors().and()
                                        .exceptionHandling().and()
                                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                        .and()
                                        .formLogin().disable();
                            } catch (Exception e) {
                                throw new SecurityException(e);
                            }
                        }
                );

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @PostConstruct
    private void setJwtSecretKey() {
        JwtUtils.setSecretKey(Constants.JWT_SECRET_KEY);
    }
}