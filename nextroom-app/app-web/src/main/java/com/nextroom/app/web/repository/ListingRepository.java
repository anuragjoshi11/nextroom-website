package com.nextroom.app.web.repository;

import com.nextroom.app.web.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}