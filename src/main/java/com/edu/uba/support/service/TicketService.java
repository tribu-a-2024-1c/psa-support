package com.edu.uba.support.service;

import com.edu.uba.support.dto.AssignResourceDto;
import com.edu.uba.support.dto.CreateTicketDto;
import com.edu.uba.support.dto.TaskDto;
import com.edu.uba.support.dto.TicketDto;
import com.edu.uba.support.model.ProductVersion;
import com.edu.uba.support.model.Resource;
import com.edu.uba.support.model.Task;
import com.edu.uba.support.model.Ticket;
import com.edu.uba.support.repository.ProductVersionRepository;
import com.edu.uba.support.repository.ResourceRepository;
import com.edu.uba.support.repository.TaskRepository;
import com.edu.uba.support.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

	private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

	private final TicketRepository ticketRepository;
	private final RestTemplate restTemplate;
	private final String projectsServiceUrl;
	private final TaskRepository taskRepository;
	private final ResourceRepository resourceRepository;
	private final ProductVersionRepository productVersionRepository;

	@Autowired
	public TicketService(TicketRepository ticketRepository, RestTemplate restTemplate, @Value("${projects.api.url}") String projectsServiceUrl, TaskRepository taskRepository, ResourceRepository resourceRepository, ProductVersionRepository productVersionRepository) {
		this.ticketRepository = ticketRepository;
		this.restTemplate = restTemplate;
		this.projectsServiceUrl = projectsServiceUrl;
		this.taskRepository = taskRepository;
		this.resourceRepository = resourceRepository;
		this.productVersionRepository = productVersionRepository;
	}

	@Transactional
	public Ticket createTicket(CreateTicketDto createTicketDto) {
		logger.info("📦 Creating ticket with title: {}", createTicketDto.getTitle());
		Optional<Ticket> existingTicket = ticketRepository.findByTitle(createTicketDto.getTitle());
		if (existingTicket.isPresent()) {
			logger.error("❌ A ticket with the title '{}' already exists", createTicketDto.getTitle());
			throw new IllegalStateException("A ticket with that title already exists");
		}

		Optional<ProductVersion> productVersion = productVersionRepository.findById(createTicketDto.getProductVersionId());
		if (productVersion.isEmpty()) {
			logger.error("❌ Product version with id '{}' does not exist", createTicketDto.getProductVersionId());
			throw new IllegalStateException("Product version does not exist");
		}

		Ticket ticket = mapTicket(createTicketDto, new Ticket());
		ticket.setProductVersion(productVersion.get());

		// Assign resource if provided
		if (createTicketDto.getResource() != null) {
			Resource resource = resourceRepository.findById(createTicketDto.getResource().getLegajo())
					.orElseGet(() -> {
						logger.info("📦 Creating new resource with id: {}", createTicketDto.getResource().getLegajo());
						Resource newResource = new Resource();
						newResource.setId(createTicketDto.getResource().getLegajo());
						newResource.setName(createTicketDto.getResource().getNombre());
						newResource.setLastName(createTicketDto.getResource().getApellido());
						return resourceRepository.save(newResource);
					});
			ticket.setResource(resource);
			resourceRepository.save(resource); //fix dont save on el ticket?
		}

		ticket = ticketRepository.save(ticket); // Save the ticket to generate its ID

		// Assign tasks if provided
		if (createTicketDto.getTaskIds() != null && !createTicketDto.getTaskIds().isEmpty()) {
			for (Long taskId : createTicketDto.getTaskIds()) {
				logger.info("🔍 Fetching task with id: {}", taskId);
				String url = projectsServiceUrl + "/projects/task/" + taskId;
				TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);

				if (taskDto == null) {
					logger.error("❌ The task with id '{}' does not exist", taskId);
					throw new IllegalStateException("The task does not exist");
				}

				logger.info("🔗 Assigning ticket to task with id: {}", taskId);
				String projectApiUrl = projectsServiceUrl + "/projects/tasks/" + taskId + "/assignTicket";
				restTemplate.postForObject(projectApiUrl, ticket, String.class); // Send ticket with generated ID

				Task task = new Task(taskDto.getId(), taskDto.getTitle(), ticket);
				taskRepository.save(task);
			}
		}
		ticket = ticketRepository.save(ticket);
		logger.info("✅ Ticket created successfully with id: {}", ticket.getId());
		return ticket;
	}


	@Transactional
	public Ticket addTaskToTicket(Long ticketId, Long taskId) {
		logger.info("🔍 Adding task with id '{}' to ticket with id '{}'", taskId, ticketId);
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if (optionalTicket.isEmpty()) {
			logger.error("❌ The ticket with id '{}' does not exist", ticketId);
			throw new IllegalStateException("The ticket does not exist");
		}

		Ticket ticket = optionalTicket.get();

		boolean taskExists = ticket.getTasks().stream()
				.anyMatch(existingTask -> existingTask.getId().equals(taskId));

		if (taskExists) {
			logger.error("❌ The task with id '{}' is already assigned to the ticket", taskId);
			throw new IllegalStateException("The task is already assigned to the ticket");
		}

		logger.info("🔍 Fetching task with id: {}", taskId);
		String url = projectsServiceUrl + "/projects/task/" + taskId;
		TaskDto registeredTask = restTemplate.getForObject(url, TaskDto.class);
		if (registeredTask == null) {
			logger.error("❌ The task with id '{}' does not exist", taskId);
			throw new IllegalStateException("The task does not exist");
		}

		logger.info("🔗 Assigning ticket to task with id: {}", taskId);
		String projectApiUrl = projectsServiceUrl + "/projects/tasks/" + taskId + "/assignTicket";
		restTemplate.postForObject(projectApiUrl, ticket, String.class);

		Task task = new Task(registeredTask.getId(), registeredTask.getTitle(), ticket);
		ticket.getTasks().add(task);

		taskRepository.save(task);
		Ticket savedTicket = ticketRepository.save(ticket);
		logger.info("✅ Task with id '{}' added to ticket with id '{}'", taskId, ticketId);
		return savedTicket;
	}

	@Transactional
	public Ticket assignTicket(Long ticketId, AssignResourceDto resourceDto) {
		logger.info("🔍 Assigning resource to ticket with id: {}", ticketId);
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if (optionalTicket.isEmpty()) {
			logger.error("❌ The ticket with id '{}' does not exist", ticketId);
			throw new IllegalStateException("The ticket does not exist");
		}
		Ticket ticket = optionalTicket.get();

		Resource resource = resourceRepository.findById(resourceDto.getLegajo())
				.orElseGet(() -> {
					logger.info("📦 Creating new resource with id: {}", resourceDto.getLegajo());
					Resource newResource = new Resource();
					newResource.setId(resourceDto.getLegajo());
					newResource.setName(resourceDto.getNombre());
					newResource.setLastName(resourceDto.getApellido());
					return resourceRepository.save(newResource);
				});

		if (ticket.getResource() != null) {
			ticket.getResource().removeTicket(ticket);
		}

		resource.addTicket(ticket);
		ticket.setResource(resource);

		ticket = ticketRepository.save(ticket);
		logger.info("✅ Resource with id '{}' assigned to ticket with id '{}'", resourceDto.getLegajo(), ticketId);
		return ticket;
	}

	@Transactional
	public Ticket finalizeTicket(Long ticketId) {
		logger.info("🔍 Finalizing ticket with id: {}", ticketId);
		Optional<Ticket> ticket = ticketRepository.findById(ticketId);
		if (ticket.isEmpty()) {
			logger.error("❌ The ticket with id '{}' does not exist", ticketId);
			throw new IllegalStateException("The ticket does not exist");
		}
		for (Task task : ticket.get().getTasks()) {
			Long taskId = task.getId();
			String url = projectsServiceUrl + "/task/" + taskId;
			TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);
			if (taskDto == null) {
				logger.error("❌ The task with id '{}' does not exist", taskId);
				throw new IllegalStateException("The task does not exist");
			}
			if (!taskDto.getStatus().equals("Finalizado")) {
				logger.error("❌ Task with id '{}' is not finalized", taskId);
				throw new IllegalStateException("All tasks must be finalized before finalizing the ticket");
			}
		}
		ticket.get().setStatus("Finalizado");
		Ticket finalizedTicket = ticketRepository.save(ticket.get());
		logger.info("✅ Ticket with id '{}' finalized successfully", ticketId);
		return finalizedTicket;
	}

	@Transactional
	public Ticket updateTicket(Long ticketId, CreateTicketDto ticketDto) {
		logger.info("🔄 Updating ticket with id: {}", ticketId);
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if (optionalTicket.isEmpty()) {
			logger.error("❌ The ticket with id '{}' does not exist", ticketId);
			throw new IllegalStateException("The ticket does not exist");
		}
		Ticket existingTicket = optionalTicket.get();

		// Update the product version if provided
		if (ticketDto.getProductVersionId() != null) {
			Optional<ProductVersion> productVersion = productVersionRepository.findById(ticketDto.getProductVersionId());
			existingTicket.setProductVersion(productVersion.get());
		}

		// Update the resource if provided
		if (ticketDto.getResource() != null) {
			Resource resource = resourceRepository.findById(ticketDto.getResource().getLegajo())
					.orElseGet(() -> {
						Resource newResource = new Resource();
						newResource.setId(ticketDto.getResource().getLegajo());
						newResource.setName(ticketDto.getResource().getNombre());
						newResource.setLastName(ticketDto.getResource().getApellido());
						newResource.addTicket(existingTicket);
						return resourceRepository.save(newResource);
					});

			// Remove ticket from old resource if exists
			if (existingTicket.getResource() != null) {
				existingTicket.getResource().removeTicket(existingTicket);
			}

			// Add ticket to new resource
			resource.addTicket(existingTicket);
			existingTicket.setResource(resource);
		} else {
			// Remove ticket from old resource if exists and new resource is not provided
			if (existingTicket.getResource() != null) {
				existingTicket.getResource().removeTicket(existingTicket);
				existingTicket.setResource(null);
			}
		}

		// Update the tasks if provided
		if (ticketDto.getTaskIds() != null && !ticketDto.getTaskIds().isEmpty()) {
			for (Long taskId : ticketDto.getTaskIds()) {
				String url = projectsServiceUrl + "/projects/task/" + taskId;
				TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);

				if (taskDto == null) {
					logger.error("❌ The task with id '{}' does not exist", taskId);
					throw new IllegalStateException("The task does not exist");
				}

				String projectApiUrl = projectsServiceUrl + "/projects/tasks/" + taskId + "/assignTicket";
				restTemplate.postForObject(projectApiUrl, existingTicket, String.class); // Send ticket with generated ID

				Task task = new Task(taskDto.getId(), taskDto.getTitle(), existingTicket);
				taskRepository.save(task);
			}
		}

		// Map the rest of the ticket fields from the DTO
		mapTicket(ticketDto, existingTicket);

		// Save the updated ticket
		return ticketRepository.save(existingTicket);
	}

	private Ticket mapTicket(CreateTicketDto createTicketDto, Ticket ticket) {
		logger.info("🔄 Mapping CreateTicketDto to Ticket entity");
		ticket.setTitle(createTicketDto.getTitle());
		ticket.setStartDate(createTicketDto.getStartDate());
		ticket.setEndDate(createTicketDto.getEndDate());
		ticket.setStatus(createTicketDto.getStatus());
		ticket.setType(createTicketDto.getType());
		ticket.setDescription(createTicketDto.getDescription());
		ticket.setSeverity(createTicketDto.getSeverity());

		return ticket;
	}


	public List<Task> getTasksByTicket(Long ticketId) {
		logger.info("🔍 Fetching tasks for ticket with id: {}", ticketId);
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if (optionalTicket.isEmpty()) {
			logger.error("❌ The ticket with id '{}' does not exist", ticketId);
			throw new IllegalStateException("The ticket does not exist");
		}
		List<Task> tasks = new ArrayList<>(optionalTicket.get().getTasks());
		logger.info("✅ Found {} tasks for ticket with id: {}", tasks.size(), ticketId);
		return tasks;
	}

	public TicketDto getTicketById(Long ticketId) {
		logger.info("🔍 Fetching ticket with id: {}", ticketId);
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if (optionalTicket.isEmpty()) {
			logger.error("❌ The ticket with id '{}' does not exist", ticketId);
			throw new IllegalStateException("The ticket does not exist");
		}
		Ticket ticket = optionalTicket.get();
		logger.info("✅ Ticket with id '{}' fetched successfully", ticketId);
		return mapToDto(ticket);
	}

	private TicketDto mapToDto(Ticket ticket) {
		TicketDto dto = new TicketDto();
		dto.setId(ticket.getId());
		dto.setTitle(ticket.getTitle());
		dto.setStartDate(ticket.getStartDate());
		dto.setEndDate(ticket.getEndDate());
		dto.setStatus(ticket.getStatus());
		dto.setType(ticket.getType());
		dto.setDescription(ticket.getDescription());
		dto.setSeverity(ticket.getSeverity());
		dto.setProductVersion(ticket.getProductVersion());
		dto.setTasks(ticket.getTasks());
		dto.setResource(ticket.getResource());
		return dto;
	}

	public List<TicketDto> getTickets() {
		logger.info("🔍 Fetching all tickets");
		List<Ticket> tickets = ticketRepository.findAll();
		return tickets.stream().map(this::mapToDto).collect(Collectors.toList());
	}
}
