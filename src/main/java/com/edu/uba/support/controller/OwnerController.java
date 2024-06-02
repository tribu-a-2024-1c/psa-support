package com.edu.uba.support.controller;

import com.edu.uba.support.dto.CreateOwnerDto;
import com.edu.uba.support.dto.AdoptedOwnerDto;
import com.edu.uba.support.model.Owner;
import com.edu.uba.support.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {

	private static final Logger log = LoggerFactory.getLogger(OwnerController.class);
	private final OwnerService ownerService;

	@Autowired
	public OwnerController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}

	@PostMapping
	@Operation(summary = "Create a new owner")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Owner created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input")
	})
	public ResponseEntity<Owner> createOwner(@RequestBody CreateOwnerDto createOwnerDto) {
		Owner owner = ownerService.createOwner(createOwnerDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(owner);
	}

	@GetMapping
	@Operation(summary = "Get all owners")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Owners retrieved successfully")
	})
	public ResponseEntity<List<Owner>> getAllOwners() {
		List<Owner> owners = ownerService.getAllOwners();
		log.info("Owners: {}", owners);
		return ResponseEntity.ok(owners);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get an owner by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Owner retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	public ResponseEntity<Owner> getOwnerById(@PathVariable Long id) {
		try {
			Owner owner = ownerService.getOwnerById(id);
			return ResponseEntity.ok(owner);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an owner")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Owner updated successfully"),
			@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	public ResponseEntity<Owner> updateOwner(@PathVariable Long id, @RequestBody CreateOwnerDto createOwnerDto) {
		try {
			Owner updatedOwner = ownerService.updateOwner(id, createOwnerDto);
			log.info("Owner updated http: {}", updatedOwner);
			return ResponseEntity.ok(updatedOwner);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete an owner")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Owner deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
		try {
			ownerService.deleteOwner(id);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/{id}/adopt")
	@Operation(summary = "Adopt a pet")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Pet adopted successfully"),
			@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	public ResponseEntity<AdoptedOwnerDto> adoptPet(@PathVariable Long id, @RequestParam Long petId) {
		try {
			AdoptedOwnerDto adoptedOwner = ownerService.adoptPet(id, petId);
			return ResponseEntity.ok(adoptedOwner);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/{id}/talk")
	@Operation(summary = "Talk with the pet API")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Talked with pet API successfully"),
			@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	public ResponseEntity<String> talkWithPetApi(@PathVariable Long id, @RequestParam String message) {
		try {
			String response = ownerService.talkWithPetApi(id, message);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			log.error("Error occurred while talking with pet API: ", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
