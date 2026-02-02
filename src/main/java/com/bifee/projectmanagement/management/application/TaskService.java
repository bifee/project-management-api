package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    public TaskService(TaskRepository taskRepository, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }

    @Transactional
    public Task createTask(Long ProjectId, CreateTaskRequest request, Long creatorId){
        projectService.getProjectById(ProjectId);
        if(!projectService.isUserMemberOfProject(ProjectId, creatorId)){
            throw new IllegalArgumentException("User is not member of project");
        }
        Task task = new Task.Builder().withTitle(request.title())
                .withDescription(request.description())
                .withProject(ProjectId)
                .withPriority(request.priority())
                .withAssignedUser(request.assignedUsersId())
                .build();
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }

    @Transactional
    public List<Task> getTasksByProjectId(Long projectId){
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        if (tasks.isEmpty()){
            throw new IllegalArgumentException("No tasks found for project with id: " + projectId);
        }
        return tasks;
    }

    @Transactional
    public Task updateTask(Long taskId, UpdateTaskRequest request, Long requesterId){
        Task task = getTaskById(taskId);
        if(!projectService.isUserMemberOfProject(task.projectId(), requesterId)){
            throw new IllegalArgumentException("User is not member of project");
        }
        Task.Builder builder = task.mutate();

        if(request.title() != null) builder.withTitle(request.title());
        if(request.description() != null) builder.withDescription(request.description());
        if(request.priority() != null) {builder.withPriority(request.priority());}
        if(request.status() != null) {builder.withStatus(request.status());}
        if(request.assignedUsersIds() != null) {builder.withAssignedUser(request.assignedUsersIds());}
        return taskRepository.save(builder.build());

    }

    @Transactional
    public void deleteTask(Long id, Long requesterId){
        Task task = getTaskById(id);
        if(!projectService.isUserMemberOfProject(task.projectId(), requesterId)){
            throw new IllegalArgumentException("User is not member of project");
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    public Task addComment(Long taskId, String content, Long requesterId){
        Task task = getTaskById(taskId);
        if (!projectService.isUserMemberOfProject(task.projectId(), requesterId)) {
            throw new IllegalArgumentException("User is not member of project");
        }
        Comment comment = new Comment.Builder()
                .withContent(content)
                .withCreator(requesterId).build();

        return taskRepository.save(updateTask());
    }





}
