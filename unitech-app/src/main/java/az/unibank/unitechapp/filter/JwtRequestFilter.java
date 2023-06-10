package az.unibank.unitechapp.filter;


import az.unibank.commons.exception.UnauthorizedException;
import az.unibank.unitechapp.confg.Config;
import az.unibank.commons.dto.auth.AuthData;
import az.unibank.commons.util.JwtUtils;
import az.unibank.commons.util.security.AuthDataBean;
import az.unibank.persistence.repo.UserAuthorityRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AuthDataBean authDataBean;
    private final UserAuthorityRepository userAuthorityRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (nonNull(jwt) && !request.getRequestURI().startsWith("/public/")) {
            jwt = jwt.replaceFirst("Bearer ", "");

            try {
                AuthData authData = JwtUtils.parseAccessToken(jwt);
                List<SimpleGrantedAuthority> authorities = Config.ROLE_AUTHORITIES.get(
                        authData.getSelectedRole().getId()
                );
                if (isNull(authorities)) {
                    authorities = userAuthorityRepository.findAuthoritiesByUser(authData.getSelectedRole().getId())
                            .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    Config.ROLE_AUTHORITIES.put(authData.getSelectedRole().getId(), authorities);
                }

                User userDetails = new User(authData.getPin(), "password", authorities);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                authDataBean.setUser(authData);

            } catch (UnauthorizedException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}