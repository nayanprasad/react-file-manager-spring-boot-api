package com.main.reactfilemanager.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class FolderDto {
    private final String id;
    private final String name;
    private final String parent;
    private final List<FolderDto> children;

    public FolderDto(String id, String name, String parent, List<FolderDto> children) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.children = children;
    }
}
