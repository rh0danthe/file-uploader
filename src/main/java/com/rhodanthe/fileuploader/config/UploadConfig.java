package com.rhodanthe.fileuploader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {

    private String photoDir;
    private String videoDir;
    private String otherDir;

    public Path getPhotoDir() {
        return resolvePath(photoDir);
    }

    public Path getVideoDir() {
        return resolvePath(videoDir);
    }

    public Path getOtherDir() {
        return resolvePath(otherDir);
    }

    public void setPhotoDir(String photoDir) {
        this.photoDir = photoDir;
    }

    public void setVideoDir(String videoDir) {
        this.videoDir = videoDir;
    }

    public void setOtherDir(String otherDir) {
        this.otherDir = otherDir;
    }

    private Path resolvePath(String dir) {
        return Paths.get(dir).toAbsolutePath().normalize();
    }
}
