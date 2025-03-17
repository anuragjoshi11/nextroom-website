package com.nextroom.app.controller;

import com.nextroom.app.service.CloudStorageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final CloudStorageService cloudStorageService;

    public ImageController(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    // Endpoint to generate and return signed URL for an image
    @GetMapping("/url")
    public String getSignedUrl(@RequestParam String fileName) {
        return cloudStorageService.generateSignedUrl(fileName);
    }
}
