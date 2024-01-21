package com.main.reactfilemanager.folder;


import com.main.reactfilemanager.model.requestModel.folder.CreateFolderRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

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

        return ResponseEntity.ok(Map.of("success", true, "folder", folder));
    }
}
