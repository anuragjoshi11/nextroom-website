package com.nextroom.app.service;

import com.nextroom.app.dto.ListingRequestDTO;

import java.util.List;

public interface ListingService {
    public List<ListingRequestDTO> getAllListings();

    public ListingRequestDTO getListingById(Long listingId);
}
