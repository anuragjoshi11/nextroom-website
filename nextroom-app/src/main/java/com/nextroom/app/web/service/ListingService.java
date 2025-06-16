package com.nextroom.app.web.service;

import com.nextroom.app.web.dto.ListingRequestDTO;

import java.util.List;

public interface ListingService {
    public List<ListingRequestDTO> getAllListings();

    public ListingRequestDTO getListingById(Long listingId);
}
