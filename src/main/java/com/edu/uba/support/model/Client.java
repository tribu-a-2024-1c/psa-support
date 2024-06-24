package com.edu.uba.support.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Client {

	@Id
	@Column
	private Long id;

	@Column
	private String companyName;

	@Column
	private String cuit;

	@ManyToMany
	@JoinTable(
			name = "client_product",
			joinColumns = @JoinColumn(name = "client_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id")
	)
	@JsonManagedReference // to avoid infinite recursion when serializing the object
	@JsonIgnoreProperties({"clients"})
	private Set<Product> products = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Client client = (Client) o;
		return Objects.equals(id, client.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
