package com.edu.uba.support.service;

import com.edu.uba.support.dto.CreateOwnerDto;
import com.edu.uba.support.dto.AddressDto;
import com.edu.uba.support.dto.AdoptedOwnerDto;
import com.edu.uba.support.dto.PetResponseDto;
import com.edu.uba.support.model.Address;
import com.edu.uba.support.model.Owner;
import com.edu.uba.support.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class OwnerService {

	private static final Logger log = LoggerFactory.getLogger(OwnerService.class);
	private final OwnerRepository ownerRepository;
	private final RestTemplate restTemplate;
	private final String petsServiceUrl;

	@Autowired
	public OwnerService(OwnerRepository ownerRepository, RestTemplate restTemplate, @Value("${projects.api.url}") String petsServiceUrl) {
		this.ownerRepository = ownerRepository;
		this.restTemplate = restTemplate;
		this.petsServiceUrl = petsServiceUrl;
	}

	@Transactional
	public Owner createOwner(CreateOwnerDto createOwnerDto) {
		Owner owner = mapToOwner(createOwnerDto, new Owner());
		return ownerRepository.save(owner);
	}

	public List<Owner> getAllOwners() {
		return ownerRepository.findAll();
	}

	public Owner getOwnerById(Long id) {
		return ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found with id " + id));
	}

	@Transactional
	public Owner updateOwner(Long id, CreateOwnerDto createOwnerDto) {
		try {
			Owner owner = ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found with id " + id));
			mapToOwner(createOwnerDto, owner);
			return ownerRepository.save(owner);
		} catch (Exception e) {
			log.error("Error occurred while updating owner: ", e);
			throw e;
		}
	}

	public void deleteOwner(Long id) {
		Owner owner = ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found with id " + id));
		ownerRepository.delete(owner);
	}

	public AdoptedOwnerDto adoptPet(Long ownerId, Long petId) {
		Supplier<RuntimeException> ownerNotFound = () -> new RuntimeException("Owner not found with id " + ownerId);
		Owner owner = ownerRepository.findById(ownerId).orElseThrow(ownerNotFound);
		owner.setPetId(petId);
		owner = ownerRepository.save(owner);

		return new AdoptedOwnerDto(owner.getId(), owner.getName(), owner.getPetId());
	}

	public String talkWithPetApi(Long ownerId, String message) {
		Owner owner = ownerRepository.findById(ownerId)
				.orElseThrow(() -> new RuntimeException("Owner not found with id " + ownerId));

		Long petId = owner.getPetId();
		if (petId == null) {
			throw new RuntimeException("Owner with id " + ownerId + " does not have a pet");
		}

		String url = petsServiceUrl + "/pets/" + petId + "/talk?message=" + message;
		log.info("Calling pet service with url: {}", url);
		PetResponseDto response = restTemplate.getForObject(url, PetResponseDto.class);

		if (response == null) {
			throw new RuntimeException("No response from pet service for petId " + petId);
		}

		return "Owner say to pet " + response.getName() + " and it responds " + response.getSound();
	}

	private Owner mapToOwner(CreateOwnerDto createOwnerDto, Owner owner) {
		owner.setName(createOwnerDto.getName());
		owner.setEmail(createOwnerDto.getEmail());
		owner.setPhoneNumber(createOwnerDto.getPhoneNumber());
		owner.setPetId(createOwnerDto.getPetId());

		List<Address> existingAddresses = Optional.ofNullable(owner.getAddresses()).orElseGet(ArrayList::new);

		List<Address> updatedAddresses = createOwnerDto.getAddresses().stream()
				.map(dto -> updateOrCreateAddress(existingAddresses, dto))
				.peek(address -> address.setOwner(owner))
				.toList();

		existingAddresses.clear();
		existingAddresses.addAll(updatedAddresses);

		owner.setAddresses(existingAddresses);
		return owner;
	}

	private Address updateOrCreateAddress(List<Address> existingAddresses, AddressDto addressDto) {
		return existingAddresses.stream()
				.filter(address -> address.getId().equals(addressDto.getId()))
				.findFirst()
				.map(address -> {
					address.setStreet(addressDto.getStreet());
					address.setCity(addressDto.getCity());
					address.setState(addressDto.getState());
					address.setZipCode(addressDto.getZipCode());
					return address;
				})
				.orElseGet(() -> {
					Address newAddress = new Address();
					newAddress.setStreet(addressDto.getStreet());
					newAddress.setCity(addressDto.getCity());
					newAddress.setState(addressDto.getState());
					newAddress.setZipCode(addressDto.getZipCode());
					return newAddress;
				});
	}
}
