package com.rhodanthe.fileuploader.service;

import com.rhodanthe.fileuploader.config.FileTypeValidator;
import com.rhodanthe.fileuploader.config.UploadConfig;
import com.rhodanthe.fileuploader.exception.FileUploadException;
import com.rhodanthe.fileuploader.exception.InvalidFileTypeException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UploadService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB

    private final UploadConfig uploadConfig;
    private final FileTypeValidator fileTypeValidator;

    public UploadService(UploadConfig uploadConfig, FileTypeValidator fileTypeValidator) {
        this.uploadConfig = uploadConfig;
        this.fileTypeValidator = fileTypeValidator;
    }

    public String savePhoto(MultipartFile file, String directory) {
        return saveFile("photo", file, directory, uploadConfig.getPhotoDir());
    }

    public String saveVideo(MultipartFile file, String directory) {
        return saveFile("video", file, directory, uploadConfig.getVideoDir());
    }

    public String saveOther(MultipartFile file, String directory) {
        return saveFile("other", file, directory, uploadConfig.getOtherDir());
    }

    private String saveFile(String type, MultipartFile file, String directory, Path baseDir) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("Файл пустой или не выбран");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("Файл слишком большой: "
                    + file.getSize() / (1024 * 1024) + " MB (максимум " + MAX_FILE_SIZE / (1024 * 1024) + " MB)");
        }

        String contentType = file.getContentType();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (!fileTypeValidator.isAllowed(type, contentType, originalFilename)) {
            throw new InvalidFileTypeException("Файл неподходящего типа: " + contentType + " / " + originalFilename);
        }

        if (directory == null || directory.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            directory = "Backup_" + LocalDateTime.now().format(formatter);
        }

        Path targetDir = baseDir.resolve(directory).normalize();
        try {
            Files.createDirectories(targetDir);

            String baseName = originalFilename;
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex != -1) {
                baseName = originalFilename.substring(0, dotIndex);
                extension = originalFilename.substring(dotIndex);
            }

            Path destination = targetDir.resolve(originalFilename);
            int counter = 1;
            while (Files.exists(destination)) {
                String newFilename = baseName + "_" + counter + extension;
                destination = targetDir.resolve(newFilename);
                counter++;
            }

            file.transferTo(destination);

            return "Файл " + destination.getFileName() + " успешно загружен в папку: " + targetDir;

        } catch (IOException e) {
            throw new FileUploadException("Не удалось сохранить файл: " + e.getMessage(), e);
        }
    }
}
