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
        Optional<Ticket> existingTicket = ticketRepository.findByTitulo(createTicketDto.getTitulo());
        if (existingTicket.isPresent()) {
            throw new IllegalStateException("Ya existe un ticket con ese titulo");
        }
        Ticket ticket = mapTicket(createTicketDto, new Ticket());
        return ticketRepository.save(ticket);
    }

    private Ticket mapTicket(CreateTicketDto createTicketDto, Ticket ticket) {
        ticket.setId(createTicketDto.getId());
        ticket.setTitulo(createTicketDto.getTitulo());
        ticket.setDescripcion(createTicketDto.getDescripcion());
        ticket.setClienteId(createTicketDto.getClienteId());
        ticket.setPrioridadId(createTicketDto.getPrioridadId());
        ticket.setProductoId(createTicketDto.getProductoId());
        return ticket;
    }

}
