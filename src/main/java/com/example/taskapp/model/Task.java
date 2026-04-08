package com.example.taskapp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Task {
    private static int counter = 1;

    private int id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private boolean completed;

    public Task(String title, String description) {
        this.id = counter++;
        this.title = title;
        this.description = description;
        this.completed = false;
    }
}
