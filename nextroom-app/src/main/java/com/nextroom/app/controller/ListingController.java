package com.nextroom.app.controller;

import com.nextroom.app.dto.ListingRequestDTO;
import com.nextroom.app.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @Operation(
            summary = "Get all listings",
            description = "Fetch all listings",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listings found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListingRequestDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Listings not found")
            }
    )
    @GetMapping()
    public List<ListingRequestDTO> getAllListings() {
        return listingService.getAllListings();
    }

    @Operation(
            summary = "Get a listing by ID",
            description = "Fetch a listing based on the provided ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListingRequestDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    @GetMapping("/{listingId}")
    public ListingRequestDTO getListingById(@PathVariable Long listingId) {
        return listingService.getListingById(listingId);
    }
}

