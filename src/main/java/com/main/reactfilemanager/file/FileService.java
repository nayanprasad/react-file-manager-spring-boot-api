package com.main.reactfilemanager.file;


import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@AllArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Map<String, Object>> upload(FileUploadRequest request) {

        // get authenticated user
        var authentication =  SecurityContextHolder.getContext().getAuthentication();
        var userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).get();


        var file = new File(
                request.getName(),
                request.getUrl(),
                request.getType(),
                request.getSize(),
                new Date(System.currentTimeMillis()),
                user.id,
                request.getFolder().toString()
        );
        fileRepository.insert(file);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "file", file,
                "user", user
        ));
    }
}
