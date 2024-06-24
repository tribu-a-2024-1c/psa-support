package com.edu.uba.support.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

	@Id
	@Column
	private Long id;

	@Column
	private String name;

	@Column
	private String lastName;

	@OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
	@JsonManagedReference // to avoid infinite recursion when serializing the object
	@JsonIgnoreProperties({"resource"})
	private Set<Ticket> tickets = new HashSet<>();

	public void addTicket(Ticket ticket) {
		tickets.add(ticket);
		ticket.setResource(this);
	}

	public void removeTicket(Ticket ticket) {
		tickets.remove(ticket);
		ticket.setResource(null);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Resource resource = (Resource) o;
		return Objects.equals(id, resource.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}

