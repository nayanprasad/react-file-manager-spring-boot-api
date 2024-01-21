package com.main.reactfilemanager.file;


import com.main.reactfilemanager.model.requestModel.file.FileRenameRequest;
import com.main.reactfilemanager.model.requestModel.file.FileUploadRequest;
import com.main.reactfilemanager.model.requestModel.file.MoveFileRequest;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
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

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable("fileId") String id) {
        return fileService.deleteFile(id);
    }

    @PatchMapping("/file/{fileId}")
    public ResponseEntity<Map<String, Object>> rename(@PathVariable("fileId") String id, @RequestBody FileRenameRequest request) {
        return fileService.renameFile(id, request);
    }

    @PutMapping("/file/move/{folderId}")
    public ResponseEntity<Map<String, Object>> moveFolder(@PathVariable("folderId") String id, @RequestBody MoveFileRequest request) {
        System.out.println("id: " + id);
        return fileService.moveFolder(id, request);
    }
}
