package com.bifee.projectmanagement.management.application.dto;

public record CreateProjectRequest(
        String title,
        String description,
        Long ownerId
){}
