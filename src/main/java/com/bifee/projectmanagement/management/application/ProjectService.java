package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.project.AddMembersRequest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.project.UpdateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import com.bifee.projectmanagement.shared.ForbiddenException;
import com.bifee.projectmanagement.shared.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project createProject(Long creatorId, CreateProjectRequest dto) {
        Project project = new Project.Builder()
                .withTitle(dto.title())
                .withDescription(dto.description())
                .withOwnerId(creatorId).
                withProjectStatus(ProjectStatus.IN_PROGRESS)
                .build();
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByTitle(String title) {
        return projectRepository.findAllByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByOwnerId(Long ownerId) {
        return projectRepository.findAllByOwnerId(ownerId);
    }

    @Transactional
    public Project addMembersToProject(AddMembersRequest request, Long projectId, Long requesterId) {
        Project project = getProjectById(projectId);
        if(!project.isOwner(requesterId)){
            throw new IllegalArgumentException("Only owner can add members to project");
        }

        Set<Long> alreadyMembers = request.userIds().stream()
                .filter(project::isMember)
                .collect(Collectors.toSet());

        if (!alreadyMembers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Users already members: " + alreadyMembers
            );
        }

        var updatedMembers = new HashSet<>(project.membersIds());
        updatedMembers.addAll(request.userIds());

        Project updatedProject = project.mutate().withMembersIds(updatedMembers).build();
        return projectRepository.save(updatedProject);
    }

    @Transactional
    public Project removeMemberFromProject(Long memberId, Long projectId, Long requesterId) {
        Project project = getProjectById(projectId);
        if(!project.isOwner(requesterId)){
            throw new IllegalArgumentException("Only owner can remove members from project");
        }
        if (!project.isMember(memberId)) {
            throw new IllegalArgumentException("Member not in project");
        }
        if (project.ownerId().equals(memberId)) {
            throw new IllegalArgumentException("Cannot remove owner from project");
        }

        var updatedMembers = new HashSet<>(project.membersIds());
        updatedMembers.remove(memberId);

        Project updatedProject = project.mutate().withMembersIds(updatedMembers).build();
        return projectRepository.save(updatedProject);

    }

    @Transactional
    public Project updateProject(Long ProjectId, UpdateProjectRequest dto, Long requesterId) {
        Project project = getProjectById(ProjectId);
        if (!project.isOwner(requesterId)) {
            throw new IllegalArgumentException("Only owner can update project");
        }
        Project.Builder builder = project.mutate();
        if(dto.title() != null){
            builder.withTitle(dto.title());
        }
        if(dto.description() != null) {
            builder.withDescription(dto.description());
        }
        if(dto.status() != null) {
            builder.withProjectStatus(dto.status());
        }
        return projectRepository.save(builder.build());
    }

    @Transactional
    public void deleteProject(Long id, Long requesterId) {
        Project project = getProjectById(id);
        if(!project.isOwner(requesterId)){
            throw new ForbiddenException("Only owner can delete project");
        }
        projectRepository.deleteById(id);
    }





}
