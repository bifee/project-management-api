package com.bifee.projectmanagement.dto;

import com.bifee.projectmanagement.entity.ProjectStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
