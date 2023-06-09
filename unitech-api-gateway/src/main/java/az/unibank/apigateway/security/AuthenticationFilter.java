package az.unibank.apigateway.security;
import az.unibank.commons.exception.UnauthorizedException;
import az.unibank.commons.exception.NotFoundException;

import az.unibank.commons.util.JwtUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Log4j2
@RefreshScope
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator routerValidator;

    public AuthenticationFilter(RouterValidator routerValidator) {
        super(Config.class);
        this.routerValidator = routerValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routerValidator.isOpenApiEndpoint.test(exchange.getRequest())) {
                return chain.filter(exchange);
            }

            try {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (isNull(authHeader)) {
                    throw new NotFoundException("Missing Authorization Header");
                }
                String jwt = authHeader.replaceFirst("Bearer ", "");

                JwtUtils.parseAccessToken(jwt);

            } catch (NotFoundException | UnauthorizedException e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            } catch (Exception e) {
                log.error("<authFilter> Error ", e);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {
    }
}