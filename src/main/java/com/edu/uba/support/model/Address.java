package com.edu.uba.support.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String street;
	private String city;
	private String state;
	private String zipCode;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "owner_id")
	@ToString.Exclude // to avoid stackoverflow error due to circular reference when printing the object
	@JsonBackReference // to avoid infinite recursion when serializing the object
	private Owner owner;
}
