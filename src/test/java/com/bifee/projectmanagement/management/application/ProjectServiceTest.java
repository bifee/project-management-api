package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.project.AddMembersRequest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.project.UpdateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import com.bifee.projectmanagement.shared.ForbiddenException;
import com.bifee.projectmanagement.shared.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: ProjectService")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Long ownerId = 1L;
    private Long projectId = 10L;
    private Project sampleProject;

    @BeforeEach
    void setUp() {
        sampleProject = new Project.Builder()
                .withId(projectId)
                .withTitle("Sample Project")
                .withDescription("Description")
                .withOwnerId(ownerId)
                .withMembersIds(new HashSet<>(Set.of(ownerId)))
                .withProjectStatus(ProjectStatus.IN_PROGRESS)
                .build();
    }

    @Nested
    @DisplayName("Scenario: Project Creation & Retrieval")
    class CreationRetrievalTests {
        @Test
        @DisplayName("Should create project successfully")
        void shouldCreateProject() {
            CreateProjectRequest request = new CreateProjectRequest("New Project", "Desc");
            when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

            Project result = projectService.createProject(ownerId, request);

            assertNotNull(result);
            assertEquals("New Project", result.title());
            assertEquals(ownerId, result.ownerId());
            verify(projectRepository).save(any());
        }

        @Test
        @DisplayName("Should return project when ID exists")
        void shouldReturnProject_WhenIdExists() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));

            Project result = projectService.getProjectById(projectId);

            assertNotNull(result);
            assertEquals("Sample Project", result.title());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void shouldThrowException_WhenIdDoesNotExist() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(99L));
        }
    }

    @Nested
    @DisplayName("Scenario: Membership Management")
    class MembershipTests {
        @Test
        @DisplayName("Should add members successfully")
        void shouldAddMembers() {
            AddMembersRequest request = new AddMembersRequest(Set.of(2L, 3L));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

            Project result = projectService.addMembersToProject(request, projectId);

            assertTrue(result.membersIds().contains(2L));
            assertTrue(result.membersIds().contains(3L));
            verify(projectRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Scenario: Project Deletion")
    class DeletionTests {
        @Test
        @DisplayName("Should delete project")
        void shouldDeleteProject() {
            projectService.deleteProject(projectId);
            verify(projectRepository).deleteById(projectId);
        }
    }
}
