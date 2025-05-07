package com.eventmanager.service;

import com.eventmanager.dto.ArtistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArtistService {
    
    Page<ArtistDTO> getAllArtists(Pageable pageable);
    
    ArtistDTO getArtistById(Long id);
    
    ArtistDTO createArtist(ArtistDTO artistDTO);
    
    ArtistDTO updateArtist(Long id, ArtistDTO artistDTO);
    
    void deleteArtist(Long id);
    
    Page<ArtistDTO> searchArtistsByName(String name, Pageable pageable);
    
    List<ArtistDTO> getArtistsByGenre(String genre);
}