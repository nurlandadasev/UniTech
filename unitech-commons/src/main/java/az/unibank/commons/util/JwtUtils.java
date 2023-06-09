package az.unibank.commons.util;

import az.unibank.commons.dto.auth.AuthData;
import az.unibank.commons.dto.auth.RefreshTokenData;
import az.unibank.commons.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    private static String secretKey = null;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 60;
    private static final int REFRESH_TOKEN_EXPIRATION_MINUTES = 24 * 60;

    public static void setSecretKey(String secretKey) {
        JwtUtils.secretKey = secretKey;
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static String generateAccessToken(AuthData authData) {

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("user", objectMapper.convertValue(authData, Map.class));

        return createToken(claims, ACCESS_TOKEN_EXPIRATION_MINUTES);
    }

    public static String generateRefreshToken(RefreshTokenData refreshTokenData) {

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("data", objectMapper.convertValue(refreshTokenData, Map.class));

        return createToken(claims, REFRESH_TOKEN_EXPIRATION_MINUTES);
    }

    private static String createToken(Map<String, Object> claims, int expirationTimeInMinutes) {

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (long) expirationTimeInMinutes * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    public static AuthData parseAccessToken(String accessToken) {
        try {
            Claims claims = extractAllClaims(accessToken);
            if (claims.getExpiration().before(new Date())) {
                throw new UnauthorizedException();
            }
            return objectMapper.convertValue(claims.get("user"), AuthData.class);

        } catch (Exception e) {
            if (isNull(e.getMessage()) || !e.getMessage().contains("JWT expired at")) {
                log.error("<parseAccessToken> Error: {}", e.getMessage());
            }
            throw new UnauthorizedException();
        }
    }

    public static RefreshTokenData parseRefreshToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            if (claims.getExpiration().before(new Date())) {
                throw new UnauthorizedException();
            }
            return objectMapper.convertValue(claims.get("data"), RefreshTokenData.class);

        } catch (Exception e) {
            if (isNull(e.getMessage()) || !e.getMessage().contains("JWT expired at")) {
                log.error("<parseRefreshToken> Error: {}", e.getMessage());
            }
            throw new UnauthorizedException();
        }
    }
}