package com.main.reactfilemanager.file;


import com.main.reactfilemanager.model.requestModel.file.FileRenameRequest;
import com.main.reactfilemanager.model.requestModel.file.FileUploadRequest;
import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
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
                request.getFolder()
        );

        fileRepository.insert(file);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "file", file,
                "user", user
        ));
    }

    public ResponseEntity<Map<String, Object>> deleteFile(String id) {
        boolean exists = fileRepository.existsById(id);
        if (!exists) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "file doesn't exist"
            ));
        }
        fileRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "success", true
        ));
    }

    public ResponseEntity<Map<String, Object>> renameFile(String id, FileRenameRequest request) {
        boolean exists = fileRepository.existsById(id);
        if (!exists) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "file doesn't exist"
            ));
        }
        var file = fileRepository.findById(id).get();
        file.setName(request.getName());
        fileRepository.save(file);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "file", file
        ));
    }
}
