package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.application.ProjectService;
import com.bifee.projectmanagement.management.application.TaskService;
import com.bifee.projectmanagement.management.application.dto.project.AddMembersRequest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.project.ProjectResponse;
import com.bifee.projectmanagement.management.application.dto.project.UpdateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.task.TaskResponse;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.task.Task;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProjectById(@PathVariable Long projectId){
        Project project = projectService.getProjectById(projectId);
        return ProjectResponse.from(project);
    }

    @GetMapping("/owner/{ownerId}")
    public ProjectResponse getProjectByOwnerId(@PathVariable Long ownerId){
        Project project = projectService.getProjectByOwnerId(ownerId);
        return ProjectResponse.from(project);
    }

    @GetMapping("/search")
    public ProjectResponse searchProject(@RequestParam String title){
        Project project = projectService.getProjectByTitle(title);
        return ProjectResponse.from(project);
    }

    @GetMapping("/{projectId}/tasks")
    public List<TaskResponse> getAllTasksByProjectId(@PathVariable Long projectId){
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        return TaskResponse.fromList(tasks);
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
        Long requesterId = userDetails.user().id();
        projectService.deleteProject(projectId, requesterId);
    }

    @PatchMapping("/{projectId}")
    public ProjectResponse updateProject(@PathVariable Long projectId,
                                         @RequestBody @Valid UpdateProjectRequest dto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.updateProject(projectId, dto, requesterId);
        return ProjectResponse.from(project);
    }

    @PostMapping("/{projectId}/members/")
    public ProjectResponse addMembersToProject(@PathVariable Long projectId, @RequestBody @Valid AddMembersRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.addMembersToProject(request, projectId, requesterId);
        return ProjectResponse.from(project);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ProjectResponse removeMemberFromProject(@PathVariable Long projectId, @PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.removeMemberFromProject(memberId, projectId, requesterId);
        return ProjectResponse.from(project);
    }


    @PostMapping("/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@PathVariable Long projectId,
                                   @RequestBody @Valid CreateTaskRequest request,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.createTask(projectId, request, requesterId);
        return TaskResponse.from(task);
    }






}
