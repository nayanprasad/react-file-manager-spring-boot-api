package com.main.reactfilemanager.auth;


import com.main.reactfilemanager.model.requestModel.auth.AuthenticateRequest;
import com.main.reactfilemanager.model.requestModel.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("auth");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody AuthenticateRequest request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // In a stateless JWT system, there is no server-side logout logic. The client should handle logout by discarding or invalidating the token.
        return authenticationService.logout();
    }
}
