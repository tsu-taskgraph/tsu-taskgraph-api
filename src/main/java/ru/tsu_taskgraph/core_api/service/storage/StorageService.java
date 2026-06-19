package ru.tsu_taskgraph.core_api.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    record StoredFile(Resource resource, String contentType) {
    }

    String store(MultipartFile file, StorageCategory category, String... pathSegments);

    StoredFile load(StorageCategory category, String... pathSegments);

    void delete(StorageCategory category, String... pathSegments);
}
