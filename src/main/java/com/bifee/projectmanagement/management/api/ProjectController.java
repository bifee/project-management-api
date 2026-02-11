package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.application.ProjectService;
import com.bifee.projectmanagement.management.application.dto.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.ProjectResponse;
import com.bifee.projectmanagement.management.application.dto.UpdateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProjectById(@PathVariable Long projectId){
        Project project = projectService.getProjectById(projectId);
        return ProjectResponse.from(project);
    }

    @GetMapping("/search")
    public ProjectResponse searchProject(@RequestParam String title){
        Project project = projectService.getProjectByTitle(title);
        return ProjectResponse.from(project);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest dto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long creatorId = userDetails.user().id();
        Project project = projectService.createProject(creatorId, dto);
        return ProjectResponse.from(project);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long projectId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        projectService.deleteProjectById(projectId, projectId);
    }

    @PatchMapping("/{projectId}")
    public ProjectResponse updateProject(@PathVariable Long projectId,
                                         @RequestBody @Valid UpdateProjectRequest dto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.updateProject(projectId, dto, requesterId);
        return ProjectResponse.from(project);
    }

    @PostMapping("/{projectId}/members")
    public ProjectResponse addMemberToProject(@PathVariable Long projectId, @RequestBody Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.addMemberToProject(memberId, projectId, requesterId);
        return ProjectResponse.from(project);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ProjectResponse removeMemberFromProject(@PathVariable Long projectId, @PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.removeMemberFromProject(memberId, projectId, requesterId);
        return ProjectResponse.from(project);
    }






}
