package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.project.AddMembersRequest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
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

    private Project sampleProject;
    private final Long ownerId = 1L;
    private final Long projectId = 10L;

    @BeforeEach
    void setUp() {
        sampleProject = new Project.Builder()
                .withId(projectId)
                .withTitle("Project Alpha")
                .withDescription("Description")
                .withOwnerId(ownerId)
                .build();
    }

    @Nested
    @DisplayName("Scenario: Project Creation & Retrieval")
    class BasicOperations {
        @Test
        @DisplayName("Should create project successfully")
        void shouldCreateProject_WhenDataIsValid() {
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
            assertEquals(projectId, result.id());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when project ID does not exist")
        void shouldThrowException_WhenProjectNotFound() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(99L));
        }
    }

    @Nested
    @DisplayName("Scenario: Membership Management")
    class MembershipTests {
        @Test
        @DisplayName("Should add members successfully when requester is the owner")
        void shouldAddMembers_WhenRequesterIsOwner() {
            AddMembersRequest request = new AddMembersRequest(Set.of(2L, 3L));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

            Project result = projectService.addMembersToProject(request, projectId, ownerId);

            assertTrue(result.membersIds().contains(2L));
            assertTrue(result.membersIds().contains(3L));
            verify(projectRepository).save(any());
        }

        @Test
        @DisplayName("Should throw ForbiddenException when requester is not the owner")
        void shouldThrowException_WhenAddingMembersAsNonOwner() {
            AddMembersRequest request = new AddMembersRequest(Set.of(2L));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));

            assertThrows(ForbiddenException.class, () ->
                    projectService.addMembersToProject(request, projectId, 99L));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when user is already a member")
        void shouldThrowException_WhenUserIsAlreadyMember() {
            AddMembersRequest request = new AddMembersRequest(Set.of(ownerId));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));

            assertThrows(ForbiddenException.class, () ->
                    projectService.addMembersToProject(request, projectId, ownerId));
        }
    }

    @Nested
    @DisplayName("Scenario: Project Deletion")
    class DeletionTests {
        @Test
        @DisplayName("Should delete project when requester is owner")
        void shouldDeleteProject_WhenRequesterIsOwner() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            projectService.deleteProject(projectId, ownerId);
            verify(projectRepository).deleteById(sampleProject.id());
        }

        @Test
        @DisplayName("Should throw ForbiddenException when non-owner tries to delete project")
        void shouldThrowException_WhenDeletingAsNonOwner() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            assertThrows(ForbiddenException.class, () -> projectService.deleteProject(projectId, 99L));
        }
    }
}