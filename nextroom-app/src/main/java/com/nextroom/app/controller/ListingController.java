package com.nextroom.app.controller;

import com.nextroom.app.dto.ListingRequestDTO;
import com.nextroom.app.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nextroom.app.constants.Constants.FRONTEND_ORIGIN;

@RestController
@RequestMapping("/listings")
@CrossOrigin(origins = {"http://localhost:5000", "https://nextroom-frontend.uc.r.appspot.com"})
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
    @PreAuthorize("hasRole('ROLE_STUDENT')")
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
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ListingRequestDTO getListingById(@PathVariable Long listingId) {
        return listingService.getListingById(listingId);
    }
}

