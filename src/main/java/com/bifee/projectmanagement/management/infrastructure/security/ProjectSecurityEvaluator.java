package com.bifee.projectmanagement.management.infrastructure.security;

import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
public class ProjectSecurityEvaluator {
    private final ProjectRepository projectRepository;

    public ProjectSecurityEvaluator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public boolean isOwner(Long projectId, UserDetailsImpl principal) {
        if (principal == null || projectId == null) return false;
        return projectRepository.findById(projectId)
                .map(p -> p.isOwner(principal.user().id()))
                .orElse(false);
    }

    public boolean isMember(Long projectId, UserDetailsImpl principal) {
        if (principal == null || projectId == null) return false;
        return projectRepository.findById(projectId)
                .map(p -> p.isMember(principal.user().id()))
                .orElse(false);
    }
}
