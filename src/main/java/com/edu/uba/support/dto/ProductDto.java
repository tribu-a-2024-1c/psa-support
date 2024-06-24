package com.edu.uba.support.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
	private Long id;
	private String name;
	private String version;
	private Set<ClientDto> clients;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ClientDto {
		private long id;
		private String razonSocial;
		private String cuit;
	}
}
