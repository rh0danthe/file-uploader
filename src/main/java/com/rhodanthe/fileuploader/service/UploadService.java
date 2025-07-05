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

    private final UploadConfig uploadConfig;
    private final FileTypeValidator fileTypeValidator;

    public UploadService(UploadConfig uploadConfig, FileTypeValidator fileTypeValidator) {
        this.uploadConfig = uploadConfig;
        this.fileTypeValidator = fileTypeValidator;
    }

    public String savePhoto(MultipartFile file, String directory) throws IOException {
        return saveFile("photo", file, directory, uploadConfig.getPhotoDir());
    }

    public String saveVideo(MultipartFile file, String directory) throws IOException {
        return saveFile("video", file, directory, uploadConfig.getVideoDir());
    }

    public String saveOther(MultipartFile file, String directory) throws IOException {
        return saveFile("other", file, directory, uploadConfig.getOtherDir());
    }

    private String saveFile(String type, MultipartFile file, String directory, Path baseDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("Файл пустой или не выбран");
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
        Files.createDirectories(targetDir);

        Path destination = targetDir.resolve(originalFilename);
        file.transferTo(destination);

        return "Файл " + originalFilename + " успешно загружен в папку: " + targetDir;
    }
}
