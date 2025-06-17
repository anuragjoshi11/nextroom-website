package com.nextroom.app.web.controller;

import com.nextroom.app.web.service.CloudStorageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
@CrossOrigin(origins = {"http://localhost:5000", "https://nextroom-frontend.uc.r.appspot.com", "https://www.nextroom.ca", "https://nextroom.ca", "https://dev-nextroom-frontend.ue.r.appspot.com"})
public class ImageController {

    private final CloudStorageService cloudStorageService;

    public ImageController(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    @GetMapping("/url")
    public String getSignedUrl(@RequestParam String fileName) {
        return cloudStorageService.generateSignedUrl(fileName);
    }
}
