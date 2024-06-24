package com.edu.uba.support.dto;

import com.edu.uba.support.model.ProductVersion;
import com.edu.uba.support.model.Resource;
import com.edu.uba.support.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {
	private Long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private String status;
	private String type;
	private String description;
	private String severity;
	private ProductVersion productVersion;
	private List<Task> tasks;
	private Resource resource;
}
