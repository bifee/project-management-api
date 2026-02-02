package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.UpdateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project createProject(CreateProjectRequest dto) {
        Project project = new Project.Builder()
                .withTitle(dto.title())
                .withDescription(dto.description())
                .withOwnerId(dto.ownerId()).
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
    public Project updateProject(UpdateProjectRequest dto) {
        Project project = getProjectById(dto.projectId());

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

    // Fazer sistema de permissao para apenas o dono poder deletar um projeto
    @Transactional
    public void deleteProjectById(Long id) {
        Project project = getProjectById(id);
        projectRepository.deleteById(id);
    }




}
