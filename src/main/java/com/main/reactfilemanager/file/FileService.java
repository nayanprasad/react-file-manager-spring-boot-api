package com.main.reactfilemanager.file;


import com.main.reactfilemanager.folder.Folder;
import com.main.reactfilemanager.folder.FolderRepository;
import com.main.reactfilemanager.model.requestModel.file.FileRenameRequest;
import com.main.reactfilemanager.model.requestModel.file.FileUploadRequest;
import com.main.reactfilemanager.model.requestModel.file.MoveFileRequest;
import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public ResponseEntity<Map<String, Object>> upload(FileUploadRequest request) {

        // get authenticated user
        User user = getAuthenticatedUser();

        var file = new File(
                request.getName(),
                request.getUrl(),
                request.getType(),
                request.getSize(),
                new Date(System.currentTimeMillis()),
                user.id,
                request.getFolder()
        );
        fileRepository.save(file);

        // add file to folder
        Folder parentFolder = folderRepository.findById(request.getFolder()).get();
        parentFolder.getFiles().add(file.getId());
        folderRepository.save(parentFolder);

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

        File file = fileRepository.findById(id).get();
        User user = getAuthenticatedUser();

        // check if user is owner of file
        if (!file.getOwner().equals(user.getId())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "you are not the owner of this file"
            ));
        }

        // remove file from folder
        Folder parentFolder = folderRepository.findById(file.getFolder()).orElse(null);
        parentFolder.getFiles().remove(id);
        folderRepository.save(parentFolder);

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
        File file = fileRepository.findById(id).get();
        User user = getAuthenticatedUser();

        if (!file.getOwner().equals(user.getId())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "you are not the owner of this file"
            ));
        }

        file.setName(request.getName());
        fileRepository.save(file);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "file", file
        ));
    }

    private User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).get();
    }

    public ResponseEntity<Map<String, Object>> moveFolder(String id, MoveFileRequest request) {
        File file = fileRepository.findById(id).orElse(null);
        if (file == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File not found"));
        }

        User user = getAuthenticatedUser();
        if (!Objects.equals(file.getOwner(), user.getId())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "You are not the owner of this file"));
        }

        Folder folder = folderRepository.findById(request.getFolder()).orElse(null);
        if (folder == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Folder not found"));
        }

        File updatedFile = fileRepository.findById(id).get();
        updatedFile.setFolder(folder.getId());
        fileRepository.save(updatedFile);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "File moved successfully"));
    }
}
