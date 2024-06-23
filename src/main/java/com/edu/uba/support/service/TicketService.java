package com.edu.uba.support.service;

import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.dto.ResourceDto;
import com.edu.uba.support.dto.TaskDto;
import com.edu.uba.support.model.Resource;
import com.edu.uba.support.model.Task;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.repository.TaskRepository;
import com.edu.uba.support.repository.TicketRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    private final RestTemplate restTemplate;

    private final String projectsServiceUrl;

    private final TaskRepository taskRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, RestTemplate restTemplate, @Value("${projects.api.url}") String projectsServiceUrl, TaskRepository taskRepository) {
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
        this.projectsServiceUrl = projectsServiceUrl;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Ticket createTicket(CreateTicketDto createTicketDto) {
        Optional<Ticket> existingTicket = ticketRepository.findByTitle(createTicketDto.getTitle());
        if (existingTicket.isPresent()) {
            throw new IllegalStateException("A ticket with that title already exists");
        }
        Ticket ticket = mapTicket(createTicketDto, new Ticket());

        if (createTicketDto.getTaskIds() != null && !createTicketDto.getTaskIds().isEmpty()) {
            for (Long taskId : createTicketDto.getTaskIds()) {
                String url = projectsServiceUrl + "/task/" + taskId;
                TaskDto registeredTask = restTemplate.getForObject(url, TaskDto.class);

                if (registeredTask == null) {
                    throw new IllegalStateException("The task does not exist");
                }

                String projectApiUrl = projectsServiceUrl + "/assignTicket/" + taskId;
                restTemplate.postForObject(projectApiUrl, ticket, String.class);

                Task task = new Task(registeredTask.getId(), registeredTask.getTitle());
                ticket.getTasks().add(task);

                taskRepository.save(task);
            }
        }

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long resourceId, String resourceName, String resourceLastname) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new IllegalStateException("The ticket does not exist");
        }

        Resource resource = new Resource(resourceId, resourceName, resourceLastname);
        ticket.get().setResource(resource);

        return ticketRepository.save(ticket.get());
    }

    @Transactional
    public Ticket finalizeTicket(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new IllegalStateException("The ticket does not exist");
        }
        for (Task task : ticket.get().getTasks()) {
            Long taskId = task.getId();
            String url = projectsServiceUrl + "/task/" + taskId;
            TaskDto registeredTask = restTemplate.getForObject(url, TaskDto.class);
            if (registeredTask == null) {
                throw new IllegalStateException("The task does not exist");
            }
            if (!registeredTask.getStatus().equals("Finalizado")) {
                throw new IllegalStateException("All tasks must be finalized before finalizing the ticket");
            }
        }
        ticket.get().setStatus("Finalizado");
        return ticketRepository.save(ticket.get());
    }

    @Transactional
    public Ticket addTaskToTicket(Long ticketId, Long taskId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new IllegalStateException("The ticket does not exist");
        }

        String url = projectsServiceUrl + "/task/" + taskId;
        TaskDto registeredTask = restTemplate.getForObject(url, TaskDto.class);
        if (registeredTask == null) {
            throw new IllegalStateException("The task does not exist");
        }

        String projectApiUrl = projectsServiceUrl + "/assignTicket/" + taskId;
        restTemplate.postForObject(projectApiUrl, ticket.get(), String.class);

        Task task = new Task(registeredTask.getId(), registeredTask.getTitle());
        ticket.get().getTasks().add(task);

        taskRepository.save(task);
        return ticketRepository.save(ticket.get());
    }

    // Mapea los datos del CreateTikcetDTO a la entidad Ticket
    private Ticket mapTicket(CreateTicketDto createTicketDto, Ticket ticket) {
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