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
    @Operation(summary = "Create new ticket", description = "This endpoint allows creating a new ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The ticket was created successfully"),
            @ApiResponse(responseCode = "400", description = "The ticket could not be created"),
    })
    public ResponseEntity<String> createTicket(@RequestBody CreateTicketDto createTicketDto) {
        try {
            Ticket ticket = ticketService.createTicket(createTicketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("{ticketId}/assign")
    @Operation(summary = "Assign ticket", description = "This endpoint allows assigning a ticket to a support engineer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The ticket was assigned successfully"),
            @ApiResponse(responseCode = "400", description = "The ticket could not be assigned"),
    })
    public ResponseEntity<String> assignTicket(@PathVariable Long ticketId, @RequestParam Long engineerId) {
        try {
            Ticket ticket = ticketService.assignTicket(ticketId, engineerId);
            return ResponseEntity.status(HttpStatus.OK).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("{ticketId}/transfer")
    @Operation(summary = "Transfer ticket", description = "This endpoint allows transferring a ticket to another sector or person")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The ticket was transferred successfully"),
            @ApiResponse(responseCode = "400", description = "The ticket could not be transferred"),
    })
    public ResponseEntity<String> transferTicket(@PathVariable Long ticketId, @RequestParam Long sectorId) {
        try {
            Ticket ticket = ticketService.transferTicket(ticketId, sectorId);
            return ResponseEntity.status(HttpStatus.OK).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
