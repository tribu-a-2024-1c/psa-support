package com.edu.uba.support.controller;

import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping()
    @Operation(summary = "Crear un nuevo ticket", description = "Este endpoint permite crear un nuevo ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "El ticket fue creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se pudo crear el ticket"),
    })
    public ResponseEntity<String> createTicket(@RequestBody CreateTicketDto createTicketDto) {
        try {
            Ticket ticket = ticketService.createTicket(createTicketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
