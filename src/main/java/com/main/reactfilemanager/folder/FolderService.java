package com.main.reactfilemanager.folder;


import com.main.reactfilemanager.dto.FolderDto;
import com.main.reactfilemanager.file.File;
import com.main.reactfilemanager.file.FileRepository;
import com.main.reactfilemanager.model.requestModel.folder.CreateFolderRequest;
import com.main.reactfilemanager.user.User;
import com.main.reactfilemanager.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).get();
    }

    private Map<String, Object> createFolderMap(Folder folder) {
        Map<String, Object> folderMap = new HashMap<>();
        folderMap.put("_id", folder.getId());
        folderMap.put("name", folder.getName());
        folderMap.put("parent", folder.getParent());
        return folderMap;
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

//        L file = fileRepository.findByFolder(folder.getId());
        Iterable<File> files = fileRepository.findByFolder(folder.getId());
        Iterable<Folder> folders = folderRepository.findByParent(folder.getId());

        List<Map<String, Object>> ancestorList = new ArrayList<>();
        ancestorList.add(createFolderMap(folder));

        String parent = folder.getParent();
        while (parent != null) {
            Folder parentFolder = folderRepository.findById(parent).orElse(null);
            if (parentFolder == null) {
                break; // Stop if parent folder not found
            }

            ancestorList.add(createFolderMap(parentFolder));
            parent = parentFolder.getParent();
        }

        Collections.reverse(ancestorList);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "folders", folders,
                "files", files,
                "ancestors", ancestorList
        ));
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
        Folder folder = folderRepository.findByOwnerAndName(user.getId(), "root").iterator().next();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "folder", folder
        ));
    }

    private List<FolderDto> buildFolderHierarchy(String parent, Long userId) {
        List<Folder> folders = folderRepository.findByParentAndOwner(parent, userId);
        return folders.stream()
                .map(folder -> new FolderDto(
                        folder.getId(),
                        folder.getName(),
                        folder.getParent(),
                        buildFolderHierarchy(folder.getId(), userId)))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Object> getFolderHierarchy() {
        User user = getAuthenticatedUser();
        List<FolderDto> folderStructure = buildFolderHierarchy(null, user.getId());
        return ResponseEntity.ok(folderStructure);
    }
}
