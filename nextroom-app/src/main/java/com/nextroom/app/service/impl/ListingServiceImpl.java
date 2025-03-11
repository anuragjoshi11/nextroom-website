package com.nextroom.app.service.impl;

import com.nextroom.app.dto.ListingRequestDTO;
import com.nextroom.app.model.Listing;
import com.nextroom.app.repository.ListingRepository;
import com.nextroom.app.service.ListingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nextroom.app.constants.Constants.LISTING_NOT_FOUND;

@Service
@Transactional
public class ListingServiceImpl implements ListingService {

    private final ModelMapper modelMapper;
    private final ListingRepository listingRepository;

    @Autowired
    public ListingServiceImpl (ModelMapper modelMapper, ListingRepository listingRepository) {
        this.modelMapper = modelMapper;
        this.listingRepository = listingRepository;
    }

    @Override
    public List<ListingRequestDTO> getAllListings() {
        return listingRepository.findAll().stream()
                .map(listing -> modelMapper.map(listing, ListingRequestDTO.class))
                .toList();
    }

    @Override
    public ListingRequestDTO getListingById(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException(LISTING_NOT_FOUND));
        return modelMapper.map(listing, ListingRequestDTO.class);
    }
}
