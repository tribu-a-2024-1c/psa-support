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
    @Operation(summary = "Assign ticket", description = "This endpoint allows assigning a ticket to a resource")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The ticket was assigned successfully"),
            @ApiResponse(responseCode = "400", description = "The ticket could not be assigned"),
    })
    public ResponseEntity<String> assignTicket(@PathVariable Long ticketId, Long resourceId, String resourceName, String resourceLastname) {
        try {
            Ticket ticket = ticketService.assignTicket(ticketId, resourceId, resourceName, resourceLastname);
            return ResponseEntity.status(HttpStatus.OK).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint para asignar tareas ya existentes (ticketId, tareaId) Verificar si la tarea existe en la api de proyectos y actualizarla la tarea para relacionarla con el ticket
    @PostMapping("{ticketId}/addTask")
    @Operation(summary = "Add task to ticket", description = "This endpoint allows adding a task to a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The task was added successfully"),
            @ApiResponse(responseCode = "400", description = "The task could not be added"),
    })
    public ResponseEntity<String> addTaskToTicket(@PathVariable Long ticketId, @RequestParam Long taskId) {
        try {
            Ticket ticket = ticketService.addTaskToTicket(ticketId, taskId);
            return ResponseEntity.status(HttpStatus.OK).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("{ticketId}/finalize")
    @Operation(summary = "Finalize ticket", description = "This endpoint allows finalizing a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The ticket was finalized successfully"),
            @ApiResponse(responseCode = "400", description = "The ticket could not be finalized"),
    })
    public ResponseEntity<String> finalizeTicket(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketService.finalizeTicket(ticketId);
            return ResponseEntity.status(HttpStatus.OK).body(ticket.toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
