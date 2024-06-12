package com.edu.uba.support.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketDto {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 255)
    private String severity;

    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;

    @NotNull
    @Size(max = 255)
    private String status;

    @NotNull
    @Size(max = 255)
    private String type;

    @NotNull
    private String description;

    @NotNull
    private Long priorityId;

    @NotNull
    private Long clientId;

    @NotNull
    private Long productId;

}