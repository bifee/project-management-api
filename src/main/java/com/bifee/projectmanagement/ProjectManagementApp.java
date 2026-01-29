package com.bifee.projectmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bifee.projectmanagement")
public class ProjectManagementApp {
    public static void main(String[] args) {
        SpringApplication.run(ProjectManagementApp.class, args);
    }
}