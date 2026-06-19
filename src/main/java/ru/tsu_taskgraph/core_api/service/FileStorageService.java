package ru.tsu_taskgraph.core_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.exception.FileStorageException;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.service.storage.StorageCategory;
import ru.tsu_taskgraph.core_api.service.storage.StorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService implements StorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${app.storage.local-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Не удалось создать корневую директорию для хранения файлов.", ex);
        }
    }

    @Override
    public String store(MultipartFile file, StorageCategory category, String... pathSegments) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty() || filename.contains("..")) {
                throw new FileStorageException("Не удалось сохранить файл: некорректный путь " + filename.toString());
            }

            Path categoryPath = rootLocation.resolve(category.getPath());
            Files.createDirectories(categoryPath);

            Path finalPath = categoryPath;
            if (pathSegments != null && pathSegments.length > 0) {
                for (String segment : pathSegments) {
                    finalPath = finalPath.resolve(segment);
                }
            }
            Files.createDirectories(finalPath);

            String extension = StringUtils.getFilenameExtension(filename);
            String newFilename = UUID.randomUUID() + "." + extension;

            Path targetLocation = finalPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;

        } catch (IOException e) {
            throw new FileStorageException("Не удалось сохранить файл " + filename, e);
        }
    }

    @Override
    public StoredFile load(StorageCategory category, String... pathSegments) {
        try {
            Path finalPath = rootLocation.resolve(category.getPath());
            for (String segment : pathSegments) {
                finalPath = finalPath.resolve(segment);
            }
            finalPath = finalPath.normalize();

            Resource resource = new UrlResource(finalPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(finalPath);
                return new StoredFile(resource, contentType);
            } else {
                throw new ResourceNotFoundException("Файл не найден: " + String.join("/", pathSegments));
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Файл не найден (неверный путь): " + String.join("/", pathSegments), e);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось определить тип контента для файла: " + String.join("/", pathSegments), e);
        }
    }

    @Override
    public void delete(StorageCategory category, String... pathSegments) {
        try {
            Path finalPath = rootLocation.resolve(category.getPath());
            for (String segment : pathSegments) {
                finalPath = finalPath.resolve(segment);
            }
            Files.deleteIfExists(finalPath.normalize());
        } catch (IOException e) {
            throw new FileStorageException("Не удалось удалить файл: " + String.join("/", pathSegments), e);
        }
    }
}
