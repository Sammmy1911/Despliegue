package com.icesi.bu_app.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.User;
import com.icesi.bu_app.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            // Based on our design, we'll use the user's email as the username for the authentication credential
            User user = userService.findByEmail(username);
            return new CustomUserDetails(user);
        } catch (Exception e){
            throw new UsernameNotFoundException("User not found", e);
        }
    }

}
