package com.edu.uba.support.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

	@NotNull
	private Long id;

	@NotNull
	@Size(max = 255)
	private String title;

	@NotNull
	private String status;
}
