package com.main.reactfilemanager.file;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private String name;
    private String url;
    private String type;
    private Number size;
    private ObjectId folder; // folder id
}
