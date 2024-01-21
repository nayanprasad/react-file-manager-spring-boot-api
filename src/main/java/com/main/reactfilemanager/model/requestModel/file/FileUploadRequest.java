package com.main.reactfilemanager.model.requestModel.file;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private String name;
    private String url;
    private String type;
    private Number size;
    private String folder; // folder id
}
