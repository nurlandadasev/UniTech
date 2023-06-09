package az.unibank.apigateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/auth/**",
            "/public/**"
    );

    public Predicate<ServerHttpRequest> isOpenApiEndpoint = request -> OPEN_API_ENDPOINTS
            .stream()
            .anyMatch(uri -> matches(uri, request.getURI().getPath()));

    private boolean matches(String openApiEndpoint, String requestUrl) {
        if (openApiEndpoint.endsWith("/**")) {
            return requestUrl.startsWith(openApiEndpoint.replace("/**", ""));
        } else {
            return requestUrl.equals(openApiEndpoint);
        }
    }
}