package com.main.reactfilemanager.auth;


import com.main.reactfilemanager.config.JwtService;
import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
import jakarta.websocket.OnClose;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


//    public AuthenticationService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    public ResponseEntity<Map<String, Object>> register(RegisterRequest request) {
        boolean isExist = userRepository.findByEmail(request.getEmail()).isPresent();
        if(isExist) {
            return ResponseEntity.status(200).body(Map.of(
                    "success", false,
                    "message", "email already exists"
            ));
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        userRepository.save(user);
        var token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.status(200).body(Map.of(
                "success", true,
                "user", user,
                "token", token
        ));
    }


    public ResponseEntity<Map<String, Object>> authenticate(AuthenticateRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        }
        catch(Exception e) {
            return ResponseEntity.status(200).body(Map.of(
                    "success", false,
                    "message", "invalid credential"
            ));
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.status(200).body(Map.of(
                "success", true,
                "user", user,
                "token", token
        ));
    }
}
