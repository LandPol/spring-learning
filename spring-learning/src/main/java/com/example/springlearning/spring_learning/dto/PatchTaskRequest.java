package com.example.springlearning.spring_learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class PatchTaskRequest {
    @Pattern(regexp = ".*\\S.*")
    private String title;

    @Pattern(regexp = ".*\\S.*")
    private String description;

    @Min(0) @Max(5)
    private Integer priority;

    public PatchTaskRequest(String title, String description, Integer priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
