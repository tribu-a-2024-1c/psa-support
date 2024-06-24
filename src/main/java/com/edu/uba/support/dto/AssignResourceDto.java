package com.edu.uba.support.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignResourceDto {
    private long legajo;
    private String nombre;
    private String apellido;
}
