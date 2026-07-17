package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.comment.CommentRequest;
import com.bifee.projectmanagement.management.application.dto.task.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import com.bifee.projectmanagement.shared.ForbiddenException;
import com.bifee.projectmanagement.shared.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    @Transactional
    public List<Task> getTasksByProjectId(Long projectId){
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        if (tasks.isEmpty()){
            throw new ResourceNotFoundException("Project", projectId);
        }
        return tasks;
    }

    @Transactional
    @PreAuthorize("@projectSecurity.isMember(#projectId, principal)")
    public Task createTask(Long projectId, CreateTaskRequest request){
        Task task = new Task.Builder().withTitle(request.title())
                .withDescription(request.description())
                .withProject(projectId)
                .withStatus(request.status())
                .withPriority(request.priority())
                .withAssignedUser(request.assignedUsersId())
                .build();
        return taskRepository.save(task);
    }

    @Transactional
    @PreAuthorize("@taskSecurity.canManageTask(#taskId, principal)")
    public Task updateTask(Long taskId, UpdateTaskRequest request){
        Task task = getTaskById(taskId);
        Task.Builder builder = task.mutate();

        if(request.title() != null) builder.withTitle(request.title());
        if(request.description() != null) builder.withDescription(request.description());
        if(request.priority() != null) {builder.withPriority(request.priority());}
        if(request.status() != null) {builder.withStatus(request.status());}
        if(request.assignedUsersIds() != null) {builder.withAssignedUser(request.assignedUsersIds());}
        return taskRepository.save(builder.build());

    }

    @Transactional
    @PreAuthorize("@taskSecurity.canManageTask(#id, principal)")
    public void deleteTask(Long id){
        taskRepository.deleteById(id);
    }


    @Transactional
    @PreAuthorize("@taskSecurity.canManageTask(#taskId, principal)")
    public Task addComment(Long taskId, CommentRequest request, Long requesterId){
        Task task = getTaskById(taskId);
        
        Comment comment = new Comment.Builder()
                .withContent(request.content())
                .withCreator(requesterId).build();

        List<Comment> comment_list = new ArrayList<>(task.comments());
        comment_list.add(comment);
        Task updatedTask = task.mutate().withComments(comment_list).build();

        return taskRepository.save(updatedTask);
    }

    @Transactional
    @PreAuthorize("@taskSecurity.canManageTask(#taskId, principal)")
    public Task removeComment(Long taskId, Long commentId){
        Task task = getTaskById(taskId);
        
        List<Comment> comment_list = new ArrayList<>(task.comments());
        boolean removed = comment_list.removeIf(comment -> comment.id().equals(commentId));
        if (!removed){
            throw new ResourceNotFoundException("Comment", commentId);
        }
        Task updatedTask = task.mutate().withComments(comment_list).build();
        return taskRepository.save(updatedTask);
    }

    @Transactional
    @PreAuthorize("@taskSecurity.isCommentCreator(#taskId, #commentId, principal)")
    public Task updateComment(Long taskId, Long commentId, CommentRequest request){
        Task task = getTaskById(taskId);

        List<Comment> comment_list = new ArrayList<>(task.comments());

        Comment founded_comment = comment_list.stream()
                .filter(comment -> comment.id().equals(commentId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        Comment updatedComment = founded_comment.mutate().withContent(request.content()).build();

        comment_list.set(comment_list.indexOf(founded_comment), updatedComment);
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
