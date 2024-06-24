package com.edu.uba.support.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {
	private String name;
	private String version; // Version to be created with the product
	private Set<ClientDto> clients; // List of clients to be associated with the product

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ClientDto {
		private long id;
		private String razonSocial;
		private String cuit;
	}
}
