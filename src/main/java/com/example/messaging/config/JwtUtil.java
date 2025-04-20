package com.example.messaging.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class JwtUtil {
    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());
    private final String SECRET_KEY = "yIv5j0UWxDJ4gH8vZKeFoZcyR4m78UyfbIFU7Hg0IuE=";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 *24;
    private final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
        tokenCache.put(username, token);
        LOGGER.info("Token generated and cached for username: " + username);
        return token;
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        Claims claims = getClaims(token);
        String cachedToken = tokenCache.get(username);
        boolean isValid = claims.getSubject().equals(username) &&
                cachedToken != null &&
                cachedToken.equals(token) &&
                !isTokenExpired(claims);
        if (!isValid) {
            LOGGER.warning("Invalid token for username: " + username + ", cached: " + (cachedToken != null));
        }
        return isValid;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}