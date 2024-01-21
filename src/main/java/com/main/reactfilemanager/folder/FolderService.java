package com.main.reactfilemanager.folder;


import com.main.reactfilemanager.model.requestModel.folder.CreateFolderRequest;
import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).get();
    }

    public ResponseEntity<Map<String, Object>> createFolder(CreateFolderRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Folder name is required"));
        }

        if (request.getParent() == null || request.getParent().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Parent folder id is required"));
        }

        Folder parentFolder = folderRepository.findById(request.getParent()).orElse(null);
        if (parentFolder == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Parent folder not found"));
        }

        Folder folder = new Folder(request.getName(), parentFolder.getId(), parentFolder.getOwner());
        folderRepository.save(folder);

        parentFolder.getFolders().add(folder.getId());
        folderRepository.save(parentFolder);

        return ResponseEntity.ok(Map.of("success", true, "message", "Folder created successfully"));
    }

    public ResponseEntity<Map<String, Object>> getFolderDatas(String id) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Folder not found"));
        }

        User user = getAuthenticatedUser();
        if (!Objects.equals(folder.getOwner(), user.getId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "You are not authorized to access this folder"));
        }

        return ResponseEntity.ok(Map.of("success", true, "folder", folder));
    }

    public ResponseEntity<Map<String, Object>> deleteFolder(String id) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Folder not found"));
        }

        User user = getAuthenticatedUser();
        if (!Objects.equals(folder.getOwner(), user.getId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "You are not authorized to delete this folder"));
        }

        if (Objects.equals(folder.getName(), "root")) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Root folder cannot be deleted"));
        }

        Folder parentFolder = folderRepository.findById(folder.getParent()).orElse(null);

        if (parentFolder == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Parent folder not found"));
        }

        parentFolder.getFolders().remove(folder.getId());
        folderRepository.save(parentFolder);

        folderRepository.delete(folder);

        return ResponseEntity.ok(Map.of("success", true, "message", "Folder deleted successfully"));
    }

    public ResponseEntity<Map<String, Object>> renameFolder(String id, CreateFolderRequest request) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Folder not found"));
        }

        User user = getAuthenticatedUser();
        if (!Objects.equals(folder.getOwner(), user.getId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "You are not authorized to rename this folder"));
        }

        if (Objects.equals(folder.getName(), "root")) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Root folder cannot be renamed"));
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Folder name is required"));
        }

        folder.setName(request.getName());
        folderRepository.save(folder);

        return ResponseEntity.ok(Map.of("success", true, "message", "Folder renamed successfully"));
    }

    public ResponseEntity<Map<String, Object>> getRootFolderDatas() {

        User user = getAuthenticatedUser();
        Optional<Folder> rootFolder = folderRepository.findByOwnerAndName(user.getId(), "root");

        return ResponseEntity.ok(Map.of("success", true, "folder", rootFolder.get()));
    }
}
