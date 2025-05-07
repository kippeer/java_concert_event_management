package com.eventmanager.service.impl;

import com.eventmanager.dto.VenueDTO;
import com.eventmanager.exception.ResourceNotFoundException;
import com.eventmanager.model.Venue;
import com.eventmanager.repository.VenueRepository;
import com.eventmanager.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    private VenueRepository venueRepository;

    @Override
    public Page<VenueDTO> getAllVenues(Pageable pageable) {
        return venueRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public VenueDTO getVenueById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));
        return convertToDto(venue);
    }

    @Override
    @Transactional
    public VenueDTO createVenue(VenueDTO venueDTO) {
        Venue venue = convertToEntity(venueDTO);
        Venue savedVenue = venueRepository.save(venue);
        return convertToDto(savedVenue);
    }

    @Override
    @Transactional
    public VenueDTO updateVenue(Long id, VenueDTO venueDTO) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));
        
        venue.setName(venueDTO.getName());
        venue.setDescription(venueDTO.getDescription());
        venue.setAddress(venueDTO.getAddress());
        venue.setCity(venueDTO.getCity());
        venue.setState(venueDTO.getState());
        venue.setZipCode(venueDTO.getZipCode());
        venue.setCountry(venueDTO.getCountry());
        venue.setCapacity(venueDTO.getCapacity());
        
        Venue updatedVenue = venueRepository.save(venue);
        return convertToDto(updatedVenue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));
        venueRepository.delete(venue);
    }

    @Override
    public Page<VenueDTO> searchVenuesByName(String name, Pageable pageable) {
        return venueRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDto);
    }

    @Override
    public List<VenueDTO> getVenuesByCity(String city) {
        return venueRepository.findByCity(city)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenueDTO> getVenuesByMinimumCapacity(Integer minCapacity) {
        return venueRepository.findByCapacityGreaterThanEqual(minCapacity)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private VenueDTO convertToDto(Venue venue) {
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venue.getId());
        venueDTO.setName(venue.getName());
        venueDTO.setDescription(venue.getDescription());
        venueDTO.setAddress(venue.getAddress());
        venueDTO.setCity(venue.getCity());
        venueDTO.setState(venue.getState());
        venueDTO.setZipCode(venue.getZipCode());
        venueDTO.setCountry(venue.getCountry());
        venueDTO.setCapacity(venue.getCapacity());
        venueDTO.setEventCount(venue.getEvents().size());
        venueDTO.setCreatedAt(venue.getCreatedAt());
        venueDTO.setUpdatedAt(venue.getUpdatedAt());
        return venueDTO;
    }
    
    private Venue convertToEntity(VenueDTO venueDTO) {
        Venue venue = new Venue();
        venue.setId(venueDTO.getId());
        venue.setName(venueDTO.getName());
        venue.setDescription(venueDTO.getDescription());
        venue.setAddress(venueDTO.getAddress());
        venue.setCity(venueDTO.getCity());
        venue.setState(venueDTO.getState());
        venue.setZipCode(venueDTO.getZipCode());
        venue.setCountry(venueDTO.getCountry());
        venue.setCapacity(venueDTO.getCapacity());
        return venue;
    }
}