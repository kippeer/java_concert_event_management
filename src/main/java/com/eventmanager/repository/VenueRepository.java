package com.eventmanager.repository;

import com.eventmanager.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    
    Page<Venue> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Venue> findByCity(String city);
    
    List<Venue> findByCapacityGreaterThanEqual(Integer capacity);
}