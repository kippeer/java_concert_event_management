package com.eventmanager.service.impl;

import com.eventmanager.dto.ArtistDTO;
import com.eventmanager.exception.ResourceNotFoundException;
import com.eventmanager.model.Artist;
import com.eventmanager.repository.ArtistRepository;
import com.eventmanager.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public Page<ArtistDTO> getAllArtists(Pageable pageable) {
        return artistRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public ArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        return convertToDto(artist);
    }

    @Override
    @Transactional
    public ArtistDTO createArtist(ArtistDTO artistDTO) {
        Artist artist = convertToEntity(artistDTO);
        Artist savedArtist = artistRepository.save(artist);
        return convertToDto(savedArtist);
    }

    @Override
    @Transactional
    public ArtistDTO updateArtist(Long id, ArtistDTO artistDTO) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        
        artist.setName(artistDTO.getName());
        artist.setBio(artistDTO.getBio());
        artist.setGenre(artistDTO.getGenre());
        artist.setContactEmail(artistDTO.getContactEmail());
        artist.setContactPhone(artistDTO.getContactPhone());
        artist.setImageUrl(artistDTO.getImageUrl());
        
        Artist updatedArtist = artistRepository.save(artist);
        return convertToDto(updatedArtist);
    }

    @Override
    @Transactional
    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        artistRepository.delete(artist);
    }

    @Override
    public Page<ArtistDTO> searchArtistsByName(String name, Pageable pageable) {
        return artistRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDto);
    }

    @Override
    public List<ArtistDTO> getArtistsByGenre(String genre) {
        return artistRepository.findByGenre(genre)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private ArtistDTO convertToDto(Artist artist) {
        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setId(artist.getId());
        artistDTO.setName(artist.getName());
        artistDTO.setBio(artist.getBio());
        artistDTO.setGenre(artist.getGenre());
        artistDTO.setContactEmail(artist.getContactEmail());
        artistDTO.setContactPhone(artist.getContactPhone());
        artistDTO.setImageUrl(artist.getImageUrl());
        artistDTO.setCreatedAt(artist.getCreatedAt());
        artistDTO.setUpdatedAt(artist.getUpdatedAt());
        return artistDTO;
    }
    
    private Artist convertToEntity(ArtistDTO artistDTO) {
        Artist artist = new Artist();
        artist.setId(artistDTO.getId());
        artist.setName(artistDTO.getName());
        artist.setBio(artistDTO.getBio());
        artist.setGenre(artistDTO.getGenre());
        artist.setContactEmail(artistDTO.getContactEmail());
        artist.setContactPhone(artistDTO.getContactPhone());
        artist.setImageUrl(artistDTO.getImageUrl());
        return artist;
    }
}