package com.icesi.bu_app.security;

import org.springframework.security.core.GrantedAuthority;

import com.icesi.bu_app.model.Permission;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SecurityAuthority implements GrantedAuthority{
    
    private final Permission permission;

    @Override
    public @Nullable String getAuthority() {
        return permission.getName();
    }
}
