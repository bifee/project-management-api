package com.bifee.projectmanagement.management.application;

import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.task.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
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

    private final Long projectId = 1L;
    private final Long taskId = 100L;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        Long memberId = 10L;


        sampleTask = new Task.Builder()
                .withId(taskId)
                .withTitle("Task")
                .withProject(projectId)
                .build();
    }

    @Nested
    @DisplayName("Scenario: Task Retrieval")
    class RetrievalTests {
        @Test
        @DisplayName("Should return task by ID")
        void shouldReturnTaskById() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));

            Task result = taskService.getTaskById(taskId);

            assertNotNull(result);
            assertEquals("Task", result.title());
        }

        @Test
        @DisplayName("Should return tasks by project ID")
        void shouldReturnTasksByProjectId() {
            when(taskRepository.findByProjectId(projectId)).thenReturn(List.of(sampleTask));

            List<Task> result = taskService.getTasksByProjectId(projectId);

            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Scenario: Task Creation")
    class CreateTaskTests {
        @Test
        @DisplayName("Should create task successfully")
        void shouldCreateTask() {
            CreateTaskRequest request = new CreateTaskRequest("New Task", "Desc", TaskStatus.TO_DO, TaskPriority.HIGH, Set.of());
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.createTask(projectId, request);

            assertNotNull(result);
            assertEquals("New Task", result.title());
            verify(taskRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Scenario: Comment Management")
    class UpdateTaskTests {
        @Test
        @DisplayName("Should update task successfully")
        void shouldUpdateTask() {
            UpdateTaskRequest request = new UpdateTaskRequest("Updated Task", null, null, null, null);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.updateTask(taskId, request);

            assertEquals("Updated Task", result.title());
            verify(taskRepository).save(any());
        }
    }
}
