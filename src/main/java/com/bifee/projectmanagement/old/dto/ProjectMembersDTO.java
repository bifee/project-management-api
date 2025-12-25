package com.bifee.projectmanagement.old.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMembersDTO {

    @NotEmpty
    private List<Long> memberIds;
}
