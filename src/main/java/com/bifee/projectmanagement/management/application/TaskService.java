package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    public TaskService(TaskRepository taskRepository, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
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

        List<Comment> comment_list = task.comments();
        comment_list.add(comment);
        Task updatedTask = task.mutate().withComments(comment_list).build();

        return taskRepository.save(updatedTask);
    }

    @Transactional
    public Task removeComment(Long taskId, Long commentId, Long requesterId){
        Task task = getTaskById(taskId);
        if (!projectService.isUserMemberOfProject(task.projectId(), requesterId)) {
            throw new IllegalArgumentException("User is not member of project");
        }
        List<Comment> comment_list = task.comments();
        comment_list.removeIf(comment -> comment.id().equals(commentId));
        Task updatedTask = task.mutate().withComments(comment_list).build();
        return taskRepository.save(updatedTask);
    }

    @Transactional
    public Task updateComment(Long taskId, Long commentId, String content, Long requesterId){
        Task task = getTaskById(taskId);
        if (!projectService.isUserMemberOfProject(task.projectId(), requesterId)) {
            throw new IllegalArgumentException("User is not member of project");
        }
        List<Comment> comment_list = task.comments();
        comment_list.stream().filter(comment -> comment.id().equals(commentId)).findFirst().ifPresent(comment -> comment.mutate().withContent(content).build());
        Task updatedTask = task.mutate().withComments(comment_list).build();
        return taskRepository.save(updatedTask);
    }

    @Transactional
    public List<Comment> getCommentsByTaskId(Long taskId){
        Task task = getTaskById(taskId);
        if (task.comments().isEmpty()){
            return List.of();
        }
        return task.comments();
    }

    @Transactional
    public Comment getCommentById(Long taskId, Long commentId){
        Task task = getTaskById(taskId);
        return task.getCommentById(commentId);
    }

    @Transactional
    public int getTaskCommentsCount(Long taskId){
        return getCommentsByTaskId(taskId).size();
    }





}
