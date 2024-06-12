package com.edu.uba.support.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String titulo;

    @Column(length = 255)
    private String severidad;

    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @Column(length = 255)
    private String estado;

    @Column(length = 255)
    private String tipo;

    @Lob
    private String descripcion;

    @Column
    private Long prioridadId;

    @Column
    private Long clienteId;

    @Column
    private Long productoId;

    @ManyToOne
    @JoinColumn(name = "Prioridad", insertable = false, updatable = false)
    private Prioridad prioridad;

}