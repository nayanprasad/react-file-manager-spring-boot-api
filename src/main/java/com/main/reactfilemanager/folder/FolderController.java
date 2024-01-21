package com.main.reactfilemanager.folder;


import com.main.reactfilemanager.model.requestModel.folder.CreateFolderRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/folder/new")
    public ResponseEntity<Map<String, Object>> addNewFolder(@RequestBody CreateFolderRequest request) {
        return folderService.createFolder(request);
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<Map<String, Object>> getFolderDatas(@PathVariable("folderId") String id) {
        return folderService.getFolderDatas(id);
    }
}
