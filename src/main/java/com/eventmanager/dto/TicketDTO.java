package com.eventmanager.dto;

import com.eventmanager.model.Ticket.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    
    private Long id;
    
    private String ticketNumber;
    
    private Long eventId;
    
    private String eventName;
    
    private LocalDateTime eventStartDateTime;
    
    private Long userId;
    
    private String userEmail;
    
    private String userName;
    
    private TicketStatus status;
    
    private BigDecimal price;
    
    private LocalDateTime purchaseDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}