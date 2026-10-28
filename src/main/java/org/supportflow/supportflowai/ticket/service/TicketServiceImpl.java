package org.supportflow.supportflowai.ticket.service;

import org.supportflow.supportflowai.ticket.api.dto.CreateTicketRequest;
import org.supportflow.supportflowai.ticket.api.dto.TicketResponse;
import org.supportflow.supportflowai.ticket.domain.Ticket;
import org.supportflow.supportflowai.ticket.exception.TicketNotFoundException;
import org.supportflow.supportflowai.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supportflow.supportflowai.ticket.api.dto.TicketPageResponse;
import org.supportflow.supportflowai.ticket.domain.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket(
                request.subject().trim(),
                request.description().trim()
        );

        Ticket savedTicket = ticketRepository.save(ticket);

        return TicketResponse.from(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isEmpty()) {
            throw new TicketNotFoundException(id);
        }

        Ticket ticket = ticketOptional.get();

        return TicketResponse.from(ticket);
    }


    @Override
    @Transactional(readOnly = true)
    public TicketPageResponse listTickets(
            TicketStatus status,
            Pageable pageable) {

        Page<Ticket> tickets;

        if (status == null) {
            tickets = ticketRepository.findAll(pageable);
        } else {
            tickets = ticketRepository.findAllByStatus(status, pageable);
        }

        Page<TicketResponse> ticketResponses =
                tickets.map(TicketResponse::from);

        return new TicketPageResponse(
                ticketResponses.getContent(),
                ticketResponses.getNumber(),
                ticketResponses.getSize(),
                ticketResponses.getTotalElements(),
                ticketResponses.getTotalPages(),
                ticketResponses.isLast()
        );
    }

}

