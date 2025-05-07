package com.eventmanager.controller;

import com.eventmanager.dto.TicketDTO;
import com.eventmanager.dto.request.PurchaseTicketRequest;
import com.eventmanager.dto.response.MessageResponse;
import com.eventmanager.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Page<TicketDTO>> getUserTickets(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getCurrentUserTickets(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping("/purchase")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<TicketDTO> purchaseTicket(@Valid @RequestBody PurchaseTicketRequest request) {
        return new ResponseEntity<>(ticketService.purchaseTicket(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<TicketDTO> cancelTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.cancelTicket(id));
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Page<TicketDTO>> getTicketsByEvent(
            @PathVariable Long eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByEvent(eventId, pageable));
    }

    @GetMapping("/validate/{ticketNumber}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> validateTicket(@PathVariable String ticketNumber) {
        boolean isValid = ticketService.validateTicket(ticketNumber);
        return ResponseEntity.ok(new MessageResponse(
                isValid ? "Ticket is valid" : "Invalid ticket"));
    }

    @PutMapping("/{id}/mark-used")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<TicketDTO> markTicketAsUsed(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.markTicketAsUsed(id));
    }
}