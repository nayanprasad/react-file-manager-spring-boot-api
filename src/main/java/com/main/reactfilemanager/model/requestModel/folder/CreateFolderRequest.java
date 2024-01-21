package com.main.reactfilemanager.model.requestModel.folder;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFolderRequest {
    private String name;
    private String parent;
}
