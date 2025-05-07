package com.eventmanager.repository;

import com.eventmanager.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    Page<Event> findByStatus(Event.EventStatus status, Pageable pageable);
    
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.startDateTime >= :start AND e.endDateTime <= :end")
    List<Event> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM Event e JOIN e.categories c WHERE c.id = :categoryId")
    Page<Event> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT e FROM Event e JOIN e.venue v WHERE v.city = :city")
    Page<Event> findByCity(@Param("city") String city, Pageable pageable);
}