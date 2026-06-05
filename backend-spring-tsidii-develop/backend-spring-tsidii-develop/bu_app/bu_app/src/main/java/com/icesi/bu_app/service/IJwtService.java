package com.icesi.bu_app.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.icesi.bu_app.model.User;

import io.jsonwebtoken.Claims;

public interface IJwtService {
    String generateToken(User user, Authentication authentication);

    boolean isTokenValid(String token);
    
    String extractUsername(String token);

    List<SimpleGrantedAuthority> extractAuthorities(String token);

    Integer extractUserCode(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    UserDetails getUserDetailsFromToken(String token);
    
    boolean isTokenExpired(String token);
}
