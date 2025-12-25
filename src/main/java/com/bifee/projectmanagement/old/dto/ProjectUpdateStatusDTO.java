package com.bifee.projectmanagement.old.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateStatusDTO {

    @NotNull(message = "Status cannot be null")
    private ProjectStatus status;
}
