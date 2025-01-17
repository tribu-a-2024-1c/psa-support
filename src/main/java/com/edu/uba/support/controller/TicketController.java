package com.edu.uba.support.controller;

import com.edu.uba.support.dto.AssignResourceDto;
import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.dto.TicketDto;
import com.edu.uba.support.model.Task;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketDto createTicketDto) {
        try {
            Ticket ticket = ticketService.createTicket(createTicketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{ticketId}/resource")
    @Operation(summary = "Assign a resource to a ticket")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource assigned to ticket"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<Ticket> assignResourceToTicket(@PathVariable Long ticketId, @RequestBody AssignResourceDto resourceDto) {
        try {
            Ticket updatedTicket = ticketService.assignTicket(ticketId, resourceDto);
            return ResponseEntity.ok(updatedTicket);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint para asignar tareas ya existentes (ticketId, tareaId) Verificar si la tarea existe en la api de proyectos y actualizarla la tarea para relacionarla con el ticket
    @PostMapping("{ticketId}/task")
    @Operation(summary = "Add task to ticket", description = "This endpoint allows adding a task to a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The task was added successfully"),
            @ApiResponse(responseCode = "400", description = "The task could not be added"),
    })
    public ResponseEntity<Ticket> addTaskToTicket(@PathVariable Long ticketId, @RequestParam Long taskId) {
        try {
            Ticket ticket = ticketService.addTaskToTicket(ticketId, taskId);
            return ResponseEntity.status(HttpStatus.OK).body(ticket);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("{ticketId}/finalize")
    @Operation(summary = "Finalize ticket", description = "This endpoint allows finalizing a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The ticket was finalized successfully"),
            @ApiResponse(responseCode = "400", description = "The ticket could not be finalized"),
    })
    public ResponseEntity<Ticket> finalizeTicket(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketService.finalizeTicket(ticketId);
            return ResponseEntity.status(HttpStatus.OK).body(ticket);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @Operation(summary = "Get all tickets", description = "This endpoint allows getting all tickets")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The tickets were retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "The tickets could not be retrieved"),
    })
    public ResponseEntity<List<TicketDto>> getTickets() {
        try {
            List<TicketDto> tickets = ticketService.getTickets();
            return ResponseEntity.status(HttpStatus.OK).body(tickets);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/{ticketId}/tasks")
    @Operation(summary = "Get all tasks associated with a ticket", description = "This endpoint allows getting all tasks associated with a ticket")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The tasks were retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "400", description = "The tasks could not be retrieved")
    })
    public ResponseEntity<List<Task>> getTasksByTicket(@PathVariable Long ticketId) {
        try {
            List<Task> tasks = ticketService.getTasksByTicket(ticketId);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{ticketId}/updateTicket")
    @Operation(summary = "Update an existing ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long ticketId, @RequestBody CreateTicketDto ticketDto) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(ticketId, ticketDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTicket);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get ticket by ID", description = "This endpoint allows getting a ticket by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The ticket was retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
    })
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long ticketId) {
        try {
            TicketDto ticketDto = ticketService.getTicketById(ticketId);
            return ResponseEntity.status(HttpStatus.OK).body(ticketDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
