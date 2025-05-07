package com.eventmanager.controller;

import com.eventmanager.dto.VenueDTO;
import com.eventmanager.dto.response.MessageResponse;
import com.eventmanager.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping
    public ResponseEntity<Page<VenueDTO>> getAllVenues(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(venueService.getAllVenues(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDTO> getVenueById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueDTO> createVenue(@Valid @RequestBody VenueDTO venueDTO) {
        return new ResponseEntity<>(venueService.createVenue(venueDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueDTO> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueDTO venueDTO) {
        return ResponseEntity.ok(venueService.updateVenue(id, venueDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok(new MessageResponse("Venue deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VenueDTO>> searchVenues(
            @RequestParam String name,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(venueService.searchVenuesByName(name, pageable));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<VenueDTO>> getVenuesByCity(@PathVariable String city) {
        return ResponseEntity.ok(venueService.getVenuesByCity(city));
    }

    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<VenueDTO>> getVenuesByMinimumCapacity(@PathVariable Integer minCapacity) {
        return ResponseEntity.ok(venueService.getVenuesByMinimumCapacity(minCapacity));
    }
}