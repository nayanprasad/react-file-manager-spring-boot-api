package com.main.reactfilemanager.file;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findByOwnerAndFolder(Long id, String root);

    Iterable<File> findByFolder(String id);

    Iterable<File> findByOwnerAndName(Long id, String root);
}
