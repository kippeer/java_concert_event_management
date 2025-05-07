package com.eventmanager.service;

import com.eventmanager.dto.TicketDTO;
import com.eventmanager.dto.request.PurchaseTicketRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {
    
    Page<TicketDTO> getCurrentUserTickets(Pageable pageable);
    
    TicketDTO getTicketById(Long id);
    
    TicketDTO purchaseTicket(PurchaseTicketRequest request);
    
    TicketDTO cancelTicket(Long id);
    
    Page<TicketDTO> getTicketsByEvent(Long eventId, Pageable pageable);
    
    boolean validateTicket(String ticketNumber);
    
    TicketDTO markTicketAsUsed(Long id);
}