package com.edu.uba.support.model;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Task {

	@Id
	@Column
	private Long id;

	@Column
	private String title;

	@ManyToOne
	@JoinColumn(name = "ticket_id")
	@JsonManagedReference // to avoid infinite recursion when serializing the object
	@ToString.Exclude // Exclude from Lombok's toString() to avoid circular reference
	private Ticket ticket;
}
