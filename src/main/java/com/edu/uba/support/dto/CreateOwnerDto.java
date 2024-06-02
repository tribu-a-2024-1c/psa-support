package com.edu.uba.support.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOwnerDto {
	private String name;
	private String email;
	private String phoneNumber;
	private Long petId;
	private List<AddressDto> addresses;
}

