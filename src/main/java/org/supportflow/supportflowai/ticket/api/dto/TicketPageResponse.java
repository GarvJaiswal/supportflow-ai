package org.supportflow.supportflowai.ticket.api.dto;


import java.util.List;

public record TicketPageResponse(
        List<TicketResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
