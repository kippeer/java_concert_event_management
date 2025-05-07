package com.eventmanager.dto;

import com.eventmanager.model.Event.EventStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Start date and time are required")
    @FutureOrPresent(message = "Start date and time must be in the future or present")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "End date and time are required")
    @FutureOrPresent(message = "End date and time must be in the future or present")
    private LocalDateTime endDateTime;
    
    @NotNull(message = "Venue is required")
    private Long venueId;
    
    private String venueName;
    
    private String venueAddress;
    
    private String city;
    
    private EventStatus status;
    
    private Integer maxAttendees;
    
    private BigDecimal ticketPrice;
    
    private Set<ArtistDTO> artists = new HashSet<>();
    
    private Set<Long> artistIds = new HashSet<>();
    
    private Set<CategoryDTO> categories = new HashSet<>();
    
    private Set<Long> categoryIds = new HashSet<>();
    
    private Integer availableTickets;
    
    private Integer soldTickets;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}