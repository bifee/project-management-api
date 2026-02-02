package com.bifee.projectmanagement.management.application;

public class TaskService {
    private final ProjectService projectService;
    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }
}
