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
    private Long resourceId;
    private List<Long> taskIds;
}
