package com.main.reactfilemanager.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Getter
public class AuthenticatedUser extends User {

    private String email;

    public AuthenticatedUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.email = email;
    }
}
