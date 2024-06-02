package com.edu.uba.support.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptedOwnerDto {
	private Long id;
	private String name;
	private Long petId;
}
