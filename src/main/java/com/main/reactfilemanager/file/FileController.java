package com.main.reactfilemanager.file;


import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/file")
    public String hello() {
        return "file";
    }

    @PostMapping("/file/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestBody FileUploadRequest request) {
        return fileService.upload(request);
    }
}
