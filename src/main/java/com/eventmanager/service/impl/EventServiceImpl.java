package com.eventmanager.service.impl;

import com.eventmanager.dto.EventDTO;
import com.eventmanager.exception.BadRequestException;
import com.eventmanager.exception.ResourceNotFoundException;
import com.eventmanager.model.Artist;
import com.eventmanager.model.Category;
import com.eventmanager.dto.CategoryDTO;
import com.eventmanager.dto.ArtistDTO;
import com.eventmanager.model.Event;
import com.eventmanager.model.Venue;
import com.eventmanager.repository.ArtistRepository;
import com.eventmanager.repository.CategoryRepository;
import com.eventmanager.repository.EventRepository;
import com.eventmanager.repository.VenueRepository;
import com.eventmanager.service.EventService;
import com.eventmanager.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ArtistRepository artistRepository;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public Page<EventDTO> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        return convertToDto(event);
    }

    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        // Validate event dates
        if (eventDTO.getEndDateTime().isBefore(eventDTO.getStartDateTime())) {
            throw new BadRequestException("End date cannot be before start date");
        }
        
        // Get venue
        Venue venue = venueRepository.findById(eventDTO.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", eventDTO.getVenueId()));
        
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartDateTime(eventDTO.getStartDateTime());
        event.setEndDateTime(eventDTO.getEndDateTime());
        event.setVenue(venue);
        event.setStatus(Event.EventStatus.DRAFT);
        event.setMaxAttendees(eventDTO.getMaxAttendees());
        event.setTicketPrice(eventDTO.getTicketPrice());
        
        // Set categories
        if (eventDTO.getCategoryIds() != null && !eventDTO.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : eventDTO.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
                categories.add(category);
            }
            event.setCategories(categories);
        }
        
        // Set artists
        if (eventDTO.getArtistIds() != null && !eventDTO.getArtistIds().isEmpty()) {
            Set<Artist> artists = new HashSet<>();
            for (Long artistId : eventDTO.getArtistIds()) {
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));
                artists.add(artist);
            }
            event.setArtists(artists);
        }
        
        Event savedEvent = eventRepository.save(event);
        return convertToDto(savedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        // Validate event status
        if (event.getStatus() == Event.EventStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a cancelled event");
        }
        
        // Validate event dates
        if (eventDTO.getEndDateTime().isBefore(eventDTO.getStartDateTime())) {
            throw new BadRequestException("End date cannot be before start date");
        }
        
        // Get venue if it's being changed
        if (!event.getVenue().getId().equals(eventDTO.getVenueId())) {
            Venue venue = venueRepository.findById(eventDTO.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", eventDTO.getVenueId()));
            event.setVenue(venue);
        }
        
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartDateTime(eventDTO.getStartDateTime());
        event.setEndDateTime(eventDTO.getEndDateTime());
        event.setMaxAttendees(eventDTO.getMaxAttendees());
        event.setTicketPrice(eventDTO.getTicketPrice());
        
        // Update categories
        if (eventDTO.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : eventDTO.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
                categories.add(category);
            }
            event.setCategories(categories);
        }
        
        // Update artists
        if (eventDTO.getArtistIds() != null) {
            Set<Artist> artists = new HashSet<>();
            for (Long artistId : eventDTO.getArtistIds()) {
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));
                artists.add(artist);
            }
            event.setArtists(artists);
        }
        
        Event updatedEvent = eventRepository.save(event);
        return convertToDto(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
                
        // Only allow deletion if no tickets have been sold or it's a draft
        if (event.getStatus() != Event.EventStatus.DRAFT && !event.getTickets().isEmpty()) {
            throw new BadRequestException("Cannot delete an event with sold tickets");
        }
        
        eventRepository.delete(event);
    }

    @Override
    public Page<EventDTO> searchEventsByName(String name, Pageable pageable) {
        if (name == null || name.isEmpty()) {
            return getAllEvents(pageable);
        }
        return eventRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventDTO> getEventsByCategory(Long categoryId, Pageable pageable) {
        return eventRepository.findByCategory(categoryId, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventDTO> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date");
        }
        
        // Convert the date range query to a paged result
        return (Page<EventDTO>) eventRepository.findAll(pageable)
                .map(this::convertToDto)
                .filter(eventDTO ->
                        !eventDTO.getStartDateTime().isBefore(startDate) &&
                                !eventDTO.getEndDateTime().isAfter(endDate));

    }

    @Override
    public Page<EventDTO> getEventsByCity(String city, Pageable pageable) {
        return eventRepository.findByCity(city, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public EventDTO publishEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        if (event.getStatus() != Event.EventStatus.DRAFT) {
            throw new BadRequestException("Only draft events can be published");
        }
        
        event.setStatus(Event.EventStatus.PUBLISHED);
        Event publishedEvent = eventRepository.save(event);
        return convertToDto(publishedEvent);
    }

    @Override
    @Transactional
    public EventDTO cancelEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        if (event.getStatus() == Event.EventStatus.CANCELLED) {
            throw new BadRequestException("Event is already cancelled");
        }
        
        if (event.getStatus() == Event.EventStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed event");
        }
        
        event.setStatus(Event.EventStatus.CANCELLED);
        Event cancelledEvent = eventRepository.save(event);
        return convertToDto(cancelledEvent);
    }
    
    private EventDTO convertToDto(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setStartDateTime(event.getStartDateTime());
        eventDTO.setEndDateTime(event.getEndDateTime());
        eventDTO.setVenueId(event.getVenue().getId());
        eventDTO.setVenueName(event.getVenue().getName());
        eventDTO.setVenueAddress(event.getVenue().getAddress());
        eventDTO.setCity(event.getVenue().getCity());
        eventDTO.setStatus(event.getStatus());
        eventDTO.setMaxAttendees(event.getMaxAttendees());
        eventDTO.setTicketPrice(event.getTicketPrice());
        
        // Add available and sold tickets counts
        eventDTO.setSoldTickets(event.getTickets().size());
        if (event.getMaxAttendees() != null) {
            eventDTO.setAvailableTickets(event.getMaxAttendees() - event.getTickets().size());
        }
        
        // Add categories
        eventDTO.setCategories(
            event.getCategories().stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setId(category.getId());
                    categoryDTO.setName(category.getName());
                    return categoryDTO;
                })
                .collect(Collectors.toSet())
        );
        
        // Add category IDs
        eventDTO.setCategoryIds(
            event.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet())
        );
        
        // Add artists
        eventDTO.setArtists(
            event.getArtists().stream()
                .map(artist -> {
                    ArtistDTO artistDTO = new ArtistDTO();
                    artistDTO.setId(artist.getId());
                    artistDTO.setName(artist.getName());
                    artistDTO.setGenre(artist.getGenre());
                    return artistDTO;
                })
                .collect(Collectors.toSet())
        );
        
        // Add artist IDs
        eventDTO.setArtistIds(
            event.getArtists().stream()
                .map(Artist::getId)
                .collect(Collectors.toSet())
        );
        
        eventDTO.setCreatedAt(event.getCreatedAt());
        eventDTO.setUpdatedAt(event.getUpdatedAt());
        
        return eventDTO;
    }
}