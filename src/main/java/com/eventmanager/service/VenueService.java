package com.eventmanager.service;

import com.eventmanager.dto.VenueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VenueService {
    
    Page<VenueDTO> getAllVenues(Pageable pageable);
    
    VenueDTO getVenueById(Long id);
    
    VenueDTO createVenue(VenueDTO venueDTO);
    
    VenueDTO updateVenue(Long id, VenueDTO venueDTO);
    
    void deleteVenue(Long id);
    
    Page<VenueDTO> searchVenuesByName(String name, Pageable pageable);
    
    List<VenueDTO> getVenuesByCity(String city);
    
    List<VenueDTO> getVenuesByMinimumCapacity(Integer minCapacity);
}