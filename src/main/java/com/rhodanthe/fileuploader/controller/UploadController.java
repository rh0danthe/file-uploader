package com.rhodanthe.fileuploader.controller;

import com.rhodanthe.fileuploader.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/upload")
    public String uploadForm(@RequestParam("type") String type, Model model) {
        model.addAttribute("type", type);
        return "upload-form";
    }

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("type") String type,
            @RequestParam(value = "directory", required = false) String directory,
            @RequestParam("files") MultipartFile[] files,
            RedirectAttributes redirectAttributes) {

        List<String> errorMessages = new ArrayList<>();
        List<String> successMessages = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String msg = switch (type) {
                    case "photo" -> uploadService.savePhoto(file, directory);
                    case "video" -> uploadService.saveVideo(file, directory);
                    default -> uploadService.saveOther(file, directory);
                };
                successMessages.add(msg);
            } catch (Exception e) {
                errorMessages.add("Ошибка загрузки " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        if (!errorMessages.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
        }
        if (!successMessages.isEmpty()) {
            redirectAttributes.addFlashAttribute("successMessages", successMessages);
        }

        return "redirect:/upload?type=" + type;
    }

}
