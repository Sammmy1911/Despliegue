package com.icesi.bu_app.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.icesi.bu_app.controller.rest.dto.LoginRequest;
import com.icesi.bu_app.controller.rest.dto.TokenResponse;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.security.CustomUserDetails;
import com.icesi.bu_app.service.IAuthService;
import com.icesi.bu_app.service.IJwtService;
import com.icesi.bu_app.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    
    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public TokenResponse login(LoginRequest loginRequest) throws BadRequestException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        
        if(userDetails == null){
            throw new BadRequestException("Invalid email");
        }

        if(!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())){
            throw new BadRequestException("Invalid password");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = customUserDetails.getUser();
    String token = jwtService.generateToken(user,
        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()));
        return new TokenResponse(token);
    }

}
