package com.bifee.projectmanagement.old.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreateRequestDTO {
    @NotNull(message = "Project name cannot be null")
    @Size(min = 3, max = 100, message = "Project name must have between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private List<Long> memberIds;
}
