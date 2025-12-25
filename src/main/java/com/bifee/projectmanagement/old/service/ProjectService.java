package com.bifee.projectmanagement.old.service;


import com.bifee.projectmanagement.old.dto.ProjectCreateRequestDTO;
import com.bifee.projectmanagement.old.dto.ProjectUpdateRequestDTO;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.old.exception.ProjectAlreadyExistsException;
import com.bifee.projectmanagement.old.repository.ProjectRepository;
import com.bifee.projectmanagement.old.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Project createProject(ProjectCreateRequestDTO dto, Long ownerId){
        if(projectRepository.existsByNameIgnoreCase(dto.getName())){
            throw new ProjectAlreadyExistsException("Project with name " + dto.getName() + " already exists");
        }
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setMembers(userRepository.findAllById(dto.getMemberIds()));
        project.setStatus(ProjectStatus.IN_PROGRESS);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException("User with id " + ownerId + " does not exist"));
        project.setOwner(owner);

        return projectRepository.save(project);
    }

    public Project updateProject(ProjectUpdateRequestDTO dto){

    }


}
