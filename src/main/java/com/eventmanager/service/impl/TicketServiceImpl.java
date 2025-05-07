package com.eventmanager.service.impl;

import com.eventmanager.dto.TicketDTO;
import com.eventmanager.dto.request.PurchaseTicketRequest;
import com.eventmanager.exception.BadRequestException;
import com.eventmanager.exception.ResourceNotFoundException;
import com.eventmanager.model.Event;
import com.eventmanager.model.Ticket;
import com.eventmanager.model.User;
import com.eventmanager.repository.EventRepository;
import com.eventmanager.repository.TicketRepository;
import com.eventmanager.service.TicketService;
import com.eventmanager.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public Page<TicketDTO> getCurrentUserTickets(Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        return ticketRepository.findByUser(currentUser, pageable)
                .map(this::convertToDto);
    }

    @Override
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        
        // Check if the current user is authorized to view this ticket
        User currentUser = securityUtils.getCurrentUser();
        if (!ticket.getUser().getId().equals(currentUser.getId()) && 
            !securityUtils.isAdmin() && 
            !securityUtils.isOrganizer()) {
            throw new BadRequestException("Not authorized to view this ticket");
        }
        
        return convertToDto(ticket);
    }

    @Override
    @Transactional
    public TicketDTO purchaseTicket(PurchaseTicketRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", request.getEventId()));
        
        // Check if event is published
        if (event.getStatus() != Event.EventStatus.PUBLISHED) {
            throw new BadRequestException("Cannot purchase tickets for a non-published event");
        }
        
        // Check if event date is in the past
        if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot purchase tickets for past events");
        }
        
        // Check if there are enough tickets available
        int soldTickets = event.getTickets().size();
        if (event.getMaxAttendees() != null && 
            soldTickets + request.getQuantity() > event.getMaxAttendees()) {
            throw new BadRequestException("Not enough tickets available");
        }
        
        // Create ticket(s)
        List<Ticket> purchasedTickets = new ArrayList<>();
        for (int i = 0; i < request.getQuantity(); i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent(event);
            ticket.setUser(currentUser);
            ticket.setStatus(Ticket.TicketStatus.PAID); // In a real app, this would be RESERVED until payment is processed
            ticket.setPrice(event.getTicketPrice());
            ticket.setPurchaseDate(LocalDateTime.now());
            purchasedTickets.add(ticketRepository.save(ticket));
        }
        
        // Return the first ticket's details
        return convertToDto(purchasedTickets.get(0));
    }

    @Override
    @Transactional
    public TicketDTO cancelTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        
        User currentUser = securityUtils.getCurrentUser();
        
        // Check if user is authorized to cancel this ticket
        if (!ticket.getUser().getId().equals(currentUser.getId()) && 
            !securityUtils.isAdmin() && 
            !securityUtils.isOrganizer()) {
            throw new BadRequestException("Not authorized to cancel this ticket");
        }
        
        // Check if ticket is already cancelled or used
        if (ticket.getStatus() == Ticket.TicketStatus.CANCELLED) {
            throw new BadRequestException("Ticket is already cancelled");
        }
        
        if (ticket.getStatus() == Ticket.TicketStatus.USED) {
            throw new BadRequestException("Cannot cancel a used ticket");
        }
        
        // Check if event has already started
        if (ticket.getEvent().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel ticket for an event that has already started");
        }
        
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        Ticket cancelledTicket = ticketRepository.save(ticket);
        return convertToDto(cancelledTicket);
    }

    @Override
    public Page<TicketDTO> getTicketsByEvent(Long eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Filter tickets by event in memory since there's no direct repository method
        return ticketRepository.findAll(pageable)
                .map(this::convertToDto)
                .filter(ticketDTO -> ticketDTO.getEventId().equals(eventId));
    }

    @Override
    public boolean validateTicket(String ticketNumber) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketNumber(ticketNumber);
        
        if (!ticketOpt.isPresent()) {
            return false;
        }
        
        Ticket ticket = ticketOpt.get();
        
        // Check if ticket is valid (paid and not cancelled or used)
        return ticket.getStatus() == Ticket.TicketStatus.PAID &&
               ticket.getEvent().getStatus() == Event.EventStatus.PUBLISHED &&
               ticket.getEvent().getStartDateTime().isAfter(LocalDateTime.now().minusHours(24)) &&
               ticket.getEvent().getEndDateTime().isAfter(LocalDateTime.now());
    }

    @Override
    @Transactional
    public TicketDTO markTicketAsUsed(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        
        // Only organizers and admins can mark tickets as used
        if (!securityUtils.isAdmin() && !securityUtils.isOrganizer()) {
            throw new BadRequestException("Not authorized to mark tickets as used");
        }
        
        // Check if ticket is valid
        if (ticket.getStatus() != Ticket.TicketStatus.PAID) {
            throw new BadRequestException("Only paid tickets can be marked as used");
        }
        
        ticket.setStatus(Ticket.TicketStatus.USED);
        Ticket usedTicket = ticketRepository.save(ticket);
        return convertToDto(usedTicket);
    }
    
    private TicketDTO convertToDto(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(ticket.getId());
        ticketDTO.setTicketNumber(ticket.getTicketNumber());
        ticketDTO.setEventId(ticket.getEvent().getId());
        ticketDTO.setEventName(ticket.getEvent().getName());
        ticketDTO.setEventStartDateTime(ticket.getEvent().getStartDateTime());
        ticketDTO.setUserId(ticket.getUser().getId());
        ticketDTO.setUserEmail(ticket.getUser().getEmail());
        ticketDTO.setUserName(ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName());
        ticketDTO.setStatus(ticket.getStatus());
        ticketDTO.setPrice(ticket.getPrice());
        ticketDTO.setPurchaseDate(ticket.getPurchaseDate());
        ticketDTO.setCreatedAt(ticket.getCreatedAt());
        ticketDTO.setUpdatedAt(ticket.getUpdatedAt());
        return ticketDTO;
    }
}