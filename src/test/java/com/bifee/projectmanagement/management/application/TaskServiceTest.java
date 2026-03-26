package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.task.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.task.*;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskService")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService taskService;

    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        project = new Project.Builder()
                .withId(1L)
                .withTitle("Project Test")
                .withOwnerId(10L)
                .withMembersIds(Set.of(10L, 20L))
                .build();

        task = new Task.Builder()
                .withId(100L)
                .withTitle("Initial Task")
                .withProject(1L)
                .build();
    }

    @Nested
    @DisplayName("Scenario: Task Creation")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task successfully when requester is a member")
        void shouldCreateTask_WhenUserIsMember() {
            CreateTaskRequest request = new CreateTaskRequest(
                    "Nova Task", "Desc", TaskStatus.TO_DO, TaskPriority.HIGH, Set.of(20L));

            when(projectService.getProjectById(1L)).thenReturn(project);
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.createTask(1L, request, 20L);

            assertNotNull(result);
            assertEquals("Nova Task", result.title());
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("Should throw ForbiddenException when creator is NOT a project member")
        void shouldThrowForbidden_WhenCreatorIsNotMember() {
            CreateTaskRequest request = new CreateTaskRequest("Erro", "Desc", null, null, null);
            when(projectService.getProjectById(1L)).thenReturn(project);

            assertThrows(ForbiddenException.class, () ->
                    taskService.createTask(1L, request, 99L));

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Scenario: Task Retrieval")
    class ListTaskTests {

        @Test
        @DisplayName("Should return list of tasks for a given project")
        void shouldReturnTasks_WhenProjectHasTasks() {
            when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));

            List<Task> result = taskService.getTasksByProjectId(1L);

            assertEquals(1, result.size());
            assertEquals(100L, result.getFirst().id());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no tasks are found")
        void shouldThrowException_WhenNoTasksFound() {
            when(taskRepository.findByProjectId(1L)).thenReturn(List.of());

            assertThrows(ResourceNotFoundException.class, () ->
                    taskService.getTasksByProjectId(1L));
        }
    }

    @Nested
    @DisplayName("Scenario: Comment Management")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update an existing comment successfully")
        void shouldUpdateTask_WhenMemberRequests() {
            UpdateTaskRequest request = new UpdateTaskRequest("New Comment Content", null, null, null, null);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(projectService.getProjectById(1L)).thenReturn(project);
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.updateTask(100L, request, 10L);

            assertEquals("New Comment Content", result.title());
            verify(taskRepository).save(any(Task.class));
        }
    }
}