package com.edu.uba.support.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketDto {

    @NotNull
    private String titulo;

    @NotNull
    private Long id;

    @NotNull
    private String descripcion;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long prioridadId;

    @NotNull
    private Long productoId;

}
