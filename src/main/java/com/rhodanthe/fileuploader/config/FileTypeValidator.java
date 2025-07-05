package com.rhodanthe.fileuploader.config;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileTypeValidator {

    private static final List<String> VIDEO_EXTENSIONS = List.of(
            ".mp4", ".avi", ".mkv", ".mov", ".webm"
    );

    public boolean isAllowed(String type, String contentType, String originalFilename) {
        if (contentType == null) return false;

        return switch (type) {
            case "photo" -> contentType.startsWith("image/");
            case "video" -> {
                boolean isVideoType = contentType.startsWith("video/") || contentType.equals("application/octet-stream");
                boolean hasVideoExtension = originalFilename != null &&
                        VIDEO_EXTENSIONS.stream().anyMatch(originalFilename::endsWith);
                yield isVideoType && hasVideoExtension;
            }
            default -> !(contentType.startsWith("image/") || contentType.startsWith("video/"));
        };
    }
}
