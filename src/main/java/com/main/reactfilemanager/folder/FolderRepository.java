package com.main.reactfilemanager.folder;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FolderRepository extends MongoRepository<Folder, String> {
    Optional<Folder> findByName(String name);

    Iterable<Folder> findByOwnerAndName(Long id, String root);

    Iterable<Folder> findByParent(String id);
}
