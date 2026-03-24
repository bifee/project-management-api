package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.task.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import com.bifee.projectmanagement.shared.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: TaskService")
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
        project = new Project.Builder().withId(10L).withOwnerId(1L).build();
        task = new Task.Builder()
                .withId(100L)
                .withTitle("Fix Bug")
                .withProject(10L)
                .withStatus(TaskStatus.TO_DO)
                .build();
    }

    @Nested
    @DisplayName("Scenario: Task Creation")
    class CreateTaskTests {
        @Test
        @DisplayName("Should create task when requester is a member")
        void shouldCreateTask_WhenRequesterIsMember() {
            CreateTaskRequest request = new CreateTaskRequest("New Task", "Desc", null, 10L);
            when(projectService.getProjectById(10L)).thenReturn(project);
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.createTask(project.id(), request, 1L);

            assertNotNull(result);
            verify(taskRepository).save(any());
        }

        @Test
        @DisplayName("Should throw ForbiddenException when requester is not a member")
        void shouldThrowException_WhenRequesterIsNotMember() {
            CreateTaskRequest request = new CreateTaskRequest("New Task", "Desc", null, 10L);
            when(projectService.getProjectById(10L)).thenReturn(project);

            assertThrows(ForbiddenException.class, () -> taskService.createTask(project.id(), request, 99L));
            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Scenario: Task Updates")
    class UpdateTaskTests {
        @Test
        @DisplayName("Should update status when requester is a member")
        void shouldUpdateStatus_WhenMemberRequests() {

            UpdateTaskRequest request = new UpdateTaskRequest(null, null, null, TaskStatus.DONE, null);
            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(projectService.getProjectById(10L)).thenReturn(project);
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.updateTaskStatus(100L, request, 1L);

            assertEquals(TaskStatus.DONE, result.status());
        }

        @Test
        @DisplayName("Should assign user to task successfully")
        void shouldAssignUser_WhenMemberRequests() {
            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(projectService.getProjectById(10L)).thenReturn(project);
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.assignTask(100L, 2L, 1L);

            assertEquals(2L, result.assignedUserId());
        }
    }
}