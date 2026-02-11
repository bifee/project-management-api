package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.management.application.TaskService;
import com.bifee.projectmanagement.management.application.dto.TaskResponse;
import com.bifee.projectmanagement.management.domain.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/task")
public class TaskController {
    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTaskById(@PathVariable Long taskId){
        Task task = taskService.getTaskById(taskId);
        return TaskResponse.from(task);
    }
}
