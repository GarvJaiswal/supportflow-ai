package org.supportflow.supportflowai.ticket.api.dto;

import org.supportflow.supportflowai.ticket.domain.Ticket;
import org.supportflow.supportflowai.ticket.domain.TicketCategory;
import org.supportflow.supportflowai.ticket.domain.TicketPriority;
import org.supportflow.supportflowai.ticket.domain.TicketStatus;

import java.time.OffsetDateTime;

public record TicketResponse(
        Long id,
        String subject,
        String description,
        TicketStatus status,
        TicketPriority priority,
        TicketCategory category,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}