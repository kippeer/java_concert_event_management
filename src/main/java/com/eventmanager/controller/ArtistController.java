package com.eventmanager.controller;

import com.eventmanager.dto.ArtistDTO;
import com.eventmanager.dto.response.MessageResponse;
import com.eventmanager.service.ArtistService;
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
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping
    public ResponseEntity<Page<ArtistDTO>> getAllArtists(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(artistService.getAllArtists(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<ArtistDTO> createArtist(@Valid @RequestBody ArtistDTO artistDTO) {
        return new ResponseEntity<>(artistService.createArtist(artistDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable Long id, @Valid @RequestBody ArtistDTO artistDTO) {
        return ResponseEntity.ok(artistService.updateArtist(id, artistDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.ok(new MessageResponse("Artist deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArtistDTO>> searchArtists(
            @RequestParam String name,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(artistService.searchArtistsByName(name, pageable));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<ArtistDTO>> getArtistsByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(artistService.getArtistsByGenre(genre));
    }
}