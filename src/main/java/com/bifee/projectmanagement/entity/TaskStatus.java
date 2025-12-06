package com.bifee.projectmanagement.entity;

import jakarta.persistence.Enumerated;

public enum TaskStatus {
    TO_DO,
    IN_PROGRESS,
    REVIEW,
    DONE
}
