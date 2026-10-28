package org.supportflow.supportflowai.ticket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.supportflow.supportflowai.ticket.domain.Ticket;
import org.supportflow.supportflowai.ticket.domain.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findAllByStatus(TicketStatus status, Pageable pageable);

}
