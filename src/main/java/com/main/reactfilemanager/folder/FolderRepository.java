package com.main.reactfilemanager.folder;

import com.main.reactfilemanager.file.File;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FolderRepository extends MongoRepository<Folder, String> {
    Optional<Folder> findByName(String name);

    Optional<Folder> findByOwnerAndName(Long id, String root);
}
