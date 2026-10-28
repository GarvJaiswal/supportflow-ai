package org.supportflow.supportflowai.ticket.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long id) {
        super("Ticket with ID " + id + " was not found");
    }
}
