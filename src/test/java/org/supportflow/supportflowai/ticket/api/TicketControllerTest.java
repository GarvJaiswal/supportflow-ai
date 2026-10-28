package org.supportflow.supportflowai.ticket.api;

import org.supportflow.supportflowai.api.GlobalExceptionHandler;
import org.supportflow.supportflowai.ticket.api.dto.CreateTicketRequest;
import org.supportflow.supportflowai.ticket.api.dto.TicketPageResponse;
import org.supportflow.supportflowai.ticket.api.dto.TicketResponse;
import org.supportflow.supportflowai.ticket.domain.TicketCategory;
import org.supportflow.supportflowai.ticket.domain.TicketPriority;
import org.supportflow.supportflowai.ticket.domain.TicketStatus;
import org.supportflow.supportflowai.ticket.exception.TicketNotFoundException;
import org.supportflow.supportflowai.ticket.service.TicketService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(TicketController.class)
@Import(GlobalExceptionHandler.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    private TicketResponse sampleTicket() {
        OffsetDateTime timestamp =
                OffsetDateTime.parse("2026-10-28T10:00:00Z");

        return new TicketResponse(
                42L,
                "Password reset",
                "Email not received",
                TicketStatus.OPEN,
                TicketPriority.MEDIUM,
                TicketCategory.ACCOUNT,
                timestamp,
                timestamp
        );
    }

    @Test
    void createTicket_returns201AndLocation() throws Exception {
        when(ticketService.createTicket(any(CreateTicketRequest.class)))
                .thenReturn(sampleTicket());

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "subject": "Password reset",
                      "description": "Email not received"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        endsWith("/api/v1/tickets/42")
                ))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.subject").value("Password reset"));

        verify(ticketService).createTicket(
                argThat(request ->
                        request.subject().equals("Password reset")
                                && request.description().equals("Email not received")
                )
        );
    }

    @Test
    void createTicket_withInvalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "subject": " ",
                      "description": ""
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Request validation failed"));

        verifyNoInteractions(ticketService);
    }

    @Test
    void getTicket_whenNotFound_returns404() throws Exception {
        when(ticketService.getTicketById(999L))
                .thenThrow(new TicketNotFoundException(999L));

        mockMvc.perform(get("/api/v1/tickets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ticket not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Ticket with ID 999 was not found"));
    }

    @Test
    void listTickets_withStatusAndPagination_returns200()
            throws Exception {

        TicketResponse ticket = sampleTicket();

        TicketPageResponse pageResponse = new TicketPageResponse(
                List.of(ticket),
                0,
                2,
                23,
                12,
                false
        );

        when(ticketService.listTickets(
                eq(TicketStatus.OPEN),
                any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/tickets")
                        .param("status", "OPEN")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(42))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(23))
                .andExpect(jsonPath("$.totalPages").value(12))
                .andExpect(jsonPath("$.last").value(false));

        verify(ticketService).listTickets(
                eq(TicketStatus.OPEN),
                argThat(pageable ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 2
                                && pageable.getSort()
                                .getOrderFor("createdAt") != null
                                && pageable.getSort()
                                .getOrderFor("createdAt").isDescending()
                )
        );
    }

    @Test
    void listTickets_withInvalidStatus_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/tickets")
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid request parameter"));

        verifyNoInteractions(ticketService);
    }

    @Test
    void listTickets_withPageSizeAboveLimit_returns400()
            throws Exception {

        mockMvc.perform(get("/api/v1/tickets")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Request validation failed"));

        verifyNoInteractions(ticketService);
    }
}
