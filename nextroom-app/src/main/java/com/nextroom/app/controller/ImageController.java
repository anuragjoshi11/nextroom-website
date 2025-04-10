package com.nextroom.app.controller;

import com.nextroom.app.service.CloudStorageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
@CrossOrigin(origins = {"http://localhost:5000", "https://nextroom-frontend.uc.r.appspot.com", "https://www.nextroom.ca", "https://nextroom.ca"})
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
