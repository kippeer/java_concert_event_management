package com.eventmanager.repository;

import com.eventmanager.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Artist> findByGenre(String genre);
}