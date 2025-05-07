package com.eventmanager.repository;

import com.eventmanager.model.Event;
import com.eventmanager.model.Ticket;
import com.eventmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByEventAndStatus(Event event, Ticket.TicketStatus status);
    
    Page<Ticket> findByUser(User user, Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.status IN ('RESERVED', 'PAID')")
    long countActiveTicketsByEvent(@Param("eventId") Long eventId);
    
    Optional<Ticket> findByTicketNumber(String ticketNumber);
}