package com.main.reactfilemanager.folder;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FolderRepository extends MongoRepository<Folder, String> {
}
