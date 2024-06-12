package com.edu.uba.support.service;

import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.repository.TicketRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket createTicket(CreateTicketDto createTicketDto) {
        Optional<Ticket> existingTicket = ticketRepository.findByTitle(createTicketDto.getTitle()); // findByTitulo
        if (existingTicket.isPresent()) {
            throw new IllegalStateException("A ticket with that title already exists"); // Ya existe un ticket con ese titulo
        }
        Ticket ticket = mapTicket(createTicketDto, new Ticket());
        return ticketRepository.save(ticket);
    }

    // Mapea los datos del DTO a la entidad
    private Ticket mapTicket(CreateTicketDto createTicketDto, Ticket ticket) {
        ticket.setId(createTicketDto.getId());
        ticket.setTitle(createTicketDto.getTitle());
        ticket.setSeverity(createTicketDto.getSeverity());
        ticket.setStartDate(createTicketDto.getStartDate());
        ticket.setEndDate(createTicketDto.getEndDate());
        ticket.setStatus(createTicketDto.getStatus());
        ticket.setType(createTicketDto.getType());
        ticket.setDescription(createTicketDto.getDescription());
        ticket.setPriorityId(createTicketDto.getPriorityId());
        ticket.setClientId(createTicketDto.getClientId());
        ticket.setProductId(createTicketDto.getProductId());

        return ticket;
    }

}