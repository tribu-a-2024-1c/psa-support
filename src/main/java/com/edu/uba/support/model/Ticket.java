package com.edu.uba.support.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Ticket")
public class Ticket {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

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

    @Column(columnDefinition = "uuid")
    private UUID prioridadId;

    @Column(columnDefinition = "uuid")
    private UUID clienteId;

    @Column(columnDefinition = "uuid")
    private UUID productoId;

    @ManyToOne
    @JoinColumn(name = "prioridad_id", insertable = false, updatable = false)
    private Prioridad prioridad;

}
