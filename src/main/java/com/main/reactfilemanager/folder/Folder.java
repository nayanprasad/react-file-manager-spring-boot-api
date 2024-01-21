package com.main.reactfilemanager.folder;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Document(collection = "folders")
@AllArgsConstructor
@NoArgsConstructor
public class Folder {
    @Id
    private String id;
    private String name;
    private String parent;  // parent folder id
    private Long owner;   // user id
    private List<String> files = new ArrayList<>();
    private List<String> folders = new ArrayList<>();

    public Folder(String name, String parent, Long owner) {
        this.name = name;
        this.parent = parent;
        this.owner = owner;
    }

    public Collection<String> getFolders() {
        return folders;
    }
    public Collection<String> getFiles() {
        return files;
    }
}
