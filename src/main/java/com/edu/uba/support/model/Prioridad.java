package com.edu.uba.support.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Prioridad")
public class Prioridad {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 255)
    private String nombre;

    private int dias;
}
