package com.main.reactfilemanager.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    public Long id;
    public String name;
    public String email;
    public String password;
    public String role;

    public Date createdAt;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }
}
