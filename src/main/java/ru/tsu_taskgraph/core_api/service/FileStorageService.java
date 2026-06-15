package ru.tsu_taskgraph.core_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.exception.FileStorageException;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path storagePath;

    public FileStorageService(@Value("${app.storage.local-dir}") String avatarStorageDir) {
        this.storagePath = Paths.get(avatarStorageDir);
        try {
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                log.info("Created avatar storage directory: {}", storagePath);
            }
        } catch (IOException e) {
            throw new FileStorageException("Не удалось создать директорию для хранения аватаров", e);
        }
    }

    public String storeAvatar(MultipartFile file, String currentAvatarFilename) {
        if (file.isEmpty()) {
            throw new BadRequestException("Файл не выбран");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Разрешены только изображения");
        }

        try {
            String filename = generateFilename(contentType, currentAvatarFilename);
            Path filePath = storagePath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            log.error("Ошибка сохранения файла аватара", e);
            throw new FileStorageException("Ошибка сохранения файла аватара", e);
        }
    }

    public record StoredFile(Resource resource, String contentType) {}

    public StoredFile loadAvatar(String filename) {
        try {
            Path filePath = storagePath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return new StoredFile(resource, contentType);
            } else {
                throw new ResourceNotFoundException("Аватарка не найдена: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Аватарка не найдена (неверный путь): " + filename, e);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось определить тип контента для файла: " + filename, e);
        }
    }



    private String generateFilename(String contentType, String currentFilename) {
        String ext = contentType.split("/")[1];
        if (currentFilename != null && !currentFilename.isEmpty() && currentFilename.endsWith(ext)) {
            // Оставляем текущее имя, если расширение совпадает и имя не пустое
            return currentFilename;
        }
        // Создаем новое имя файла с уникальным идентификатором и правильным расширением
        return UUID.randomUUID() + "." + ext;
    }
}
