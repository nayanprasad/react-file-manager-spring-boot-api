package com.main.reactfilemanager.user;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<Map<String, Object>> getProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userEmail = authentication.getName();
        var User = userRepository.findByEmail(userEmail).get();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", User
        ));
    }
}
