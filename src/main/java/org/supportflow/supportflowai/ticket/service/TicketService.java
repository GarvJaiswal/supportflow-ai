package org.supportflow.supportflowai.ticket.service;

import org.supportflow.supportflowai.ticket.api.dto.CreateTicketRequest;
import org.supportflow.supportflowai.ticket.api.dto.TicketPageResponse;
import org.supportflow.supportflowai.ticket.api.dto.TicketResponse;
import org.supportflow.supportflowai.ticket.domain.TicketStatus;
import org.springframework.data.domain.Pageable;

public interface TicketService {

    TicketResponse createTicket(CreateTicketRequest request);

    TicketResponse getTicketById(Long id);

    TicketPageResponse listTickets(TicketStatus status, Pageable pageable);

}

