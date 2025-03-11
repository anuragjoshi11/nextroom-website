package com.nextroom.app.controller;

import com.nextroom.app.dto.ListingRequestDTO;
import com.nextroom.app.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/listings")
//@CrossOrigin(origins = FRONTEND_ORIGIN)
public class ListingController {

    private final ListingService listingService;

    @Autowired
    public ListingController (ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping()
    public List<ListingRequestDTO> getAllListings() {
        return listingService.getAllListings();
    }

    @GetMapping("/{listingId}")
    public ListingRequestDTO getListingById(@PathVariable Long listingId) {
        return listingService.getListingById(listingId);
    }
}

