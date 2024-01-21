package com.main.reactfilemanager.user;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/me")
    public ResponseEntity<Map<String, Object>> getProfile() {
        return userService.getProfile();
    }
}
