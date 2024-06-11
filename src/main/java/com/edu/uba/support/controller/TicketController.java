package com.edu.uba.support.controller;

import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.service.TicketService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Owner created successfully"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist")
    })
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketDto createTicketDto) {
        Ticket ticket = ticketService.createTicket(createTicketDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
