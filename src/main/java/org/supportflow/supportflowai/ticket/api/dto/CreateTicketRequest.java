package org.supportflow.supportflowai.ticket.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(

        @NotBlank(message = "Subject is required")
        @Size(
                max = 200,
                message = "Subject must not exceed 200 characters"
        )
        String subject,

        @NotBlank(message = "Description is required")
        @Size(
                max = 10000,
                message = "Description must not exceed 10000 characters"
        )
        String description

) {
}