package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.identity.infrastructure.security.SecurityConfig;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by ID", description = "Returns a single project by its ID")
    @ApiResponse(responseCode = "200", description = "Project found")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ProjectResponse getProjectById(@PathVariable Long projectId){
        Project project = projectService.getProjectById(projectId);
        return ProjectResponse.from(project);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get projects by owner ID", description = "Returns a list of projects owned by the specified owner ID")
    @ApiResponse(responseCode = "200", description = "Projects found")
    @ApiResponse(responseCode = "404", description = "No projects found for the specified owner ID")
    @ApiResponse(responseCode = "500", description = "Server error")
    public List<ProjectResponse> getProjectsByOwnerId(@PathVariable Long ownerId){
        List<Project> projects = projectService.getProjectsByOwnerId(ownerId);
        return ProjectResponse.fromList(projects);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my projects", description = "Returns a list of projects owned by the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Projects found")
    @ApiResponse(responseCode = "404", description = "No projects found for the authenticated user")
    @ApiResponse(responseCode = "500", description = "Server error")
    public List<ProjectResponse> getMyProjects(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<Project> projects = projectService.getProjectsByOwnerId(userDetails.user().id());
        return ProjectResponse.fromList(projects);
    }

    @GetMapping("/search")
    @Operation(summary = "Search projects by title", description = "Returns a list of projects that match the specified title")
    @ApiResponse(responseCode = "200", description = "Projects found")
    @ApiResponse(responseCode = "404", description = "No projects found with the specified title")
    @ApiResponse(responseCode = "500", description = "Server error")
    public List<ProjectResponse> searchProjects(@RequestParam String title){
        List<Project> projects = projectService.getProjectsByTitle(title);
        return ProjectResponse.fromList(projects);
    }


    @GetMapping("/{projectId}/tasks")
    @Operation(summary = "Get all tasks for a project", description = "Returns a list of all tasks associated with the specified project")
    @ApiResponse(responseCode = "200", description = "Tasks found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public List<TaskResponse> getAllTasksByProjectId(@PathVariable Long projectId){
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        return TaskResponse.fromList(tasks);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project", description = "Creates a new project with the provided details")
    @ApiResponse(responseCode = "201", description = "Project created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Project with the same title already exists")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest dto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long creatorId = userDetails.user().id();
        Project project = projectService.createProject(creatorId, dto);
        return ProjectResponse.from(project);
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete a project", description = "Deletes a project by its ID")
    @ApiResponse(responseCode = "204", description = "Project deleted successfully")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long projectId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        projectService.deleteProject(projectId, requesterId);
    }

    @PatchMapping("/{projectId}")
    @Operation(summary = "Update a project", description = "Updates a project by its ID with the provided details")
    @ApiResponse(responseCode = "200", description = "Project updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ProjectResponse updateProject(@PathVariable Long projectId,
                                         @RequestBody @Valid UpdateProjectRequest dto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.updateProject(projectId, dto, requesterId);
        return ProjectResponse.from(project);
    }

    @PostMapping("/{projectId}/members/")
    @Operation(summary = "Add members to a project", description = "Adds members to a project by their IDs")
    @ApiResponse(responseCode = "200", description = "Members added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Project or member not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ProjectResponse addMembersToProject(@PathVariable Long projectId, @RequestBody @Valid AddMembersRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.addMembersToProject(request, projectId, requesterId);
        return ProjectResponse.from(project);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @Operation(summary = "Remove a member from a project", description = "Removes a member from a project by their ID")
    @ApiResponse(responseCode = "200", description = "Member removed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Project or member not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ProjectResponse removeMemberFromProject(@PathVariable Long projectId, @PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Project project = projectService.removeMemberFromProject(memberId, projectId, requesterId);
        return ProjectResponse.from(project);
    }


    @PostMapping("/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task", description = "Creates a new task for a project")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public TaskResponse createTask(@PathVariable Long projectId,
                                   @RequestBody @Valid CreateTaskRequest request,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.createTask(projectId, request, requesterId);
        return TaskResponse.from(task);
    }






}
