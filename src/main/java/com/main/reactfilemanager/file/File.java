package com.main.reactfilemanager.file;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "files")
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    private String id;
    private String name;
    private String url;
    private String type;
    private Number size;
    private Date createdAt;
    private Long owner;   // user id
    private String folder; // folder id

    public File(String name, String url, String type, Number size, Date createdAt, Long owner, String folder) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
        this.createdAt = createdAt;
        this.owner = owner;
        this.folder = folder;
    }
}
