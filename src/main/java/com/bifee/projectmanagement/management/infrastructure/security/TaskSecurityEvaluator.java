package com.bifee.projectmanagement.management.infrastructure.security;

import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurityEvaluator {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskSecurityEvaluator(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public boolean canManageTask(Long taskId, UserDetailsImpl principal) {
        if (principal == null || taskId == null) return false;
        
        return taskRepository.findById(taskId)
                .flatMap(task -> projectRepository.findById(task.projectId()))
                .map(project -> project.isMember(principal.user().id()))
                .orElse(false);
    }

    public boolean isCommentCreator(Long taskId, Long commentId, UserDetailsImpl principal) {
        if (principal == null || taskId == null || commentId == null) return false;

        return taskRepository.findById(taskId)
                .map(task -> task.getCommentById(commentId))
                .map(comment -> comment.creatorId().equals(principal.user().id()))
                .orElse(false);
    }
}
