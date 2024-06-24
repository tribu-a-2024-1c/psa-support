package com.edu.uba.support.service;

import com.edu.uba.support.dto.AssignResourceDto;
import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.dto.TaskDto;
import com.edu.uba.support.model.Resource;
import com.edu.uba.support.model.Task;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.repository.ResourceRepository;
import com.edu.uba.support.repository.TaskRepository;
import com.edu.uba.support.repository.TicketRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    private final RestTemplate restTemplate;

    private final String projectsServiceUrl;

    private final TaskRepository taskRepository;

    private final ResourceRepository resourceRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, RestTemplate restTemplate, @Value("${projects.api.url}") String projectsServiceUrl, TaskRepository taskRepository, ResourceRepository resourceRepository) {
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
        this.projectsServiceUrl = projectsServiceUrl;
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
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
                TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);

                if (taskDto == null) {
                    throw new IllegalStateException("The task does not exist");
                }

                String projectApiUrl = projectsServiceUrl + "/assignTicket/" + taskId;
                restTemplate.postForObject(projectApiUrl, ticket, String.class);

                Task task = new Task(taskDto.getId(), taskDto.getTitle());
                ticket.getTasks().add(task);

                taskRepository.save(task);
            }
        }

        return ticketRepository.save(ticket);
    }


    @Transactional
    public Ticket addTaskToTicket(Long ticketId, Long taskId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            throw new IllegalStateException("The ticket does not exist");
        }

        Ticket ticket = optionalTicket.get();

        // Check if the task already exists in the ticket
        boolean taskExists = ticket.getTasks().stream()
            .anyMatch(existingTask -> existingTask.getId().equals(taskId));

        if (taskExists) {
            throw new IllegalStateException("The task is already assigned to the ticket");
        }

        // Fetch the task from the project service
        String url = projectsServiceUrl + "/projects/task/" + taskId;
        TaskDto registeredTask = restTemplate.getForObject(url, TaskDto.class);
        if (registeredTask == null) {
            throw new IllegalStateException("The task does not exist");
        }

        // Assign the ticket to the task in the project service
        String projectApiUrl = projectsServiceUrl + "/projects/tasks/" + taskId + "/assignTicket";
        restTemplate.postForObject(projectApiUrl, ticket, String.class);

        // Create a new Task entity and add it to the ticket
        Task task = new Task(registeredTask.getId(), registeredTask.getTitle());
        ticket.getTasks().add(task);

        taskRepository.save(task);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, AssignResourceDto resourceDto) {
        // Find the ticket
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            throw new IllegalStateException("The ticket does not exist");
        }
        Ticket ticket = optionalTicket.get();

        // Find or create the resource
        Resource resource = resourceRepository.findById(resourceDto.getLegajo())
            .orElseGet(() -> {
                Resource newResource = new Resource();
                newResource.setId(resourceDto.getLegajo());
                newResource.setName(resourceDto.getNombre());
                newResource.setLastName(resourceDto.getApellido());
                return resourceRepository.save(newResource);
            });

        // Remove the ticket from its old resource if it exists
        if (ticket.getResource() != null) {
            ticket.getResource().removeTicket(ticket);
        }

        // Set the new resource and add the ticket to the resource's ticket set
        resource.addTicket(ticket);

        // Save the updated ticket and resource
        ticket.setResource(resource);
        return ticketRepository.save(ticket);
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
            TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);
            if (taskDto == null) {
                throw new IllegalStateException("The task does not exist");
            }
            if (!taskDto.getStatus().equals("Finalizado")) {
                throw new IllegalStateException("All tasks must be finalized before finalizing the ticket");
            }
        }
        ticket.get().setStatus("Finalizado");
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

    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    public List<Task> getTasksByTicket(Long ticketId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            throw new IllegalStateException("The ticket does not exist");
        }
        return new ArrayList<>(optionalTicket.get().getTasks());
    }
}
