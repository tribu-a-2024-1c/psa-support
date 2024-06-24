package com.edu.uba.support.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketDto {
    private String title;
    private Date startDate;
    private Date endDate;
    private String status;
    private String type;
    private String description;
    private String priority;  // enum for priority on frontend
    private Long productVersionId;
    private Resource resource;
    private List<Long> taskIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Resource {
        private long legajo;
        private String nombre;
        private String apellido;
    }
}
