package com.icesi.bu_app.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.icesi.bu_app.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails{
    private final User user;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        //Como lo dividimos por roles y permisos, hay que añadir tanto el rol como los permisos a las authorities
        if (user.getRole() != null && user.getRole().getType() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getType()));
        }
        if (user.getRole() != null && user.getRole().getRolePermissions() != null) {
            authorities.addAll(
                user.getRole()
                    .getRolePermissions()
                    .stream()
                    .filter(rp -> rp.getPermission() != null && rp.getPermission().getName() != null)
                    .map(rp -> new SimpleGrantedAuthority(rp.getPermission().getName()))
                    .toList()
            );
        }

        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
