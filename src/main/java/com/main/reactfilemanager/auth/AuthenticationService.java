package com.main.reactfilemanager.auth;


import com.main.reactfilemanager.config.JwtService;
import com.main.reactfilemanager.folder.Folder;
import com.main.reactfilemanager.folder.FolderRepository;
import com.main.reactfilemanager.model.requestModel.auth.AuthenticateRequest;
import com.main.reactfilemanager.model.requestModel.auth.RegisterRequest;
import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
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
    private final FolderRepository folderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<Map<String, Object>> register(RegisterRequest request) {
        boolean isExist = userRepository.findByEmail(request.getEmail()).isPresent();
        if(isExist) {
            return ResponseEntity.status(403).body(Map.of(
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

        var rootFolder = new Folder("root", null, user.getId());
        folderRepository.save(rootFolder);

        return ResponseEntity.status(201).body(Map.of(
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
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "invalid credential"
            ));
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.status(200).body(Map.of(
                "success", true,
                "token", token
        ));
    }
}
