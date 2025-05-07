package com.eventmanager.service;

import com.eventmanager.dto.EventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EventService {
    
    Page<EventDTO> getAllEvents(Pageable pageable);
    
    EventDTO getEventById(Long id);
    
    EventDTO createEvent(EventDTO eventDTO);
    
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    
    void deleteEvent(Long id);
    
    Page<EventDTO> searchEventsByName(String name, Pageable pageable);
    
    Page<EventDTO> getEventsByCategory(Long categoryId, Pageable pageable);
    
    Page<EventDTO> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    Page<EventDTO> getEventsByCity(String city, Pageable pageable);
    
    EventDTO publishEvent(Long id);
    
    EventDTO cancelEvent(Long id);
}