package com.icesi.bu_app.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.User;
import com.icesi.bu_app.security.CustomUserDetails;
import com.icesi.bu_app.service.IJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long expirationTime;

    private SecretKey getSign() {
        byte[] keyBytes = hexToBytes(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] hexToBytes(String value) {
        int length = value.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("JWT secret must contain an even number of hex characters");
        }

        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
                    + Character.digit(value.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public String generateToken(User user, Authentication authentication) {
        List<String> authorities = resolveAuthorities(user, authentication);

        return Jwts.builder()
                .id(user.getCode().toString())
                .claims(
                    Map.of(
                        "code", user.getCode(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole() == null ? null : user.getRole().getType(),
                        "authorities", authorities
                    )
                )
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSign())
                .compact();
    }

    private List<String> resolveAuthorities(User user, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }

        Collection<? extends GrantedAuthority> userAuthorities = new CustomUserDetails(user).getAuthorities();
        return userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSign())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Integer extractUserCode(String token) {
        Claims claims = extractClaim(token, Function.identity());
        Number code = claims.get("code", Number.class);
        return code == null ? null : code.intValue();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractClaim(token, Function.identity());
        List<String> authorities = claims.get("authorities", List.class);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public UserDetails getUserDetailsFromToken(String token) {
        String username = extractUsername(token);
        List<SimpleGrantedAuthority> authorities = extractAuthorities(token);
        return new org.springframework.security.core.userdetails.User(username, "", authorities);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSign())
                    .build()
                    .parseSignedClaims(token);

            if (isTokenExpired(token)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
