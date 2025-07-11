package com.rhodanthe.fileuploader;

import com.rhodanthe.fileuploader.config.UploadConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(UploadConfig.class)
public class FileUploaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileUploaderApplication.class, args);
    }
}
