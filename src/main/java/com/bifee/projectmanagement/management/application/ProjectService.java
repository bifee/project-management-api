package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.UpdateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;


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
        return projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Project getProjectByTitle(String title) {
        return projectRepository.findByTitle(title).orElseThrow(() -> new IllegalArgumentException("Project not found with title: " + title));
    }

    @Transactional(readOnly = true)
    public Project getProjectByOwnerId(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId).orElseThrow(() -> new IllegalArgumentException("Project not found with ownerId: " + ownerId));
    }

    @Transactional
    public Project addMemberToProject(Long memberId, Long projectId, Long requesterId) {
        Project project = getProjectById(projectId);
        if(!isUserOwnerOfProject(projectId, requesterId)){
            throw new IllegalArgumentException("Only owner can add members to project");
        }

        if (project.membersIds().contains(memberId)) {
            throw new IllegalArgumentException("Member already in project");
        }

        var updatedMembers = new HashSet<>(project.membersIds());
        updatedMembers.add(memberId);

        Project updatedProject = project.mutate().withMembersIds(updatedMembers).build();
        return projectRepository.save(updatedProject);
    }

    @Transactional
    public Project removeMemberFromProject(Long memberId, Long projectId, Long requesterId) {
        Project project = getProjectById(projectId);
        if(!isUserOwnerOfProject(projectId, requesterId)){
            throw new IllegalArgumentException("Only owner can remove members from project");
        }
        if (!project.membersIds().contains(memberId)) {
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
        if (!isUserOwnerOfProject(ProjectId, requesterId)) {
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
    public void deleteProjectById(Long id, Long requesterId) {
        Project project = getProjectById(id);
        if(!isUserOwnerOfProject(id, requesterId)){
            throw new IllegalArgumentException("Only owner can delete project");
        }
        projectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isUserMemberOfProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        return project.membersIds().contains(userId);
    }

    @Transactional(readOnly = true)
    public boolean isUserOwnerOfProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        return project.ownerId().equals(userId);
    }




}
