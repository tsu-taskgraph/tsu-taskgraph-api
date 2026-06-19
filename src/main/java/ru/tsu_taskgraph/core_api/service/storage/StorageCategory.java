package ru.tsu_taskgraph.core_api.service.storage;

import lombok.Getter;

@Getter
public enum StorageCategory {
    AVATARS("avatars"),
    MEDIA("media");

    private final String path;

    StorageCategory(String path) {
        this.path = path;
    }

}
