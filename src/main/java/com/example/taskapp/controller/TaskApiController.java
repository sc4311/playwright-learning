package com.example.taskapp.controller;

import com.example.taskapp.model.Task;
import com.example.taskapp.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    private final TaskService taskService;

    public TaskApiController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable int id) {
        return taskService.getByIdOrThrow(id);
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        final long total = taskService.findAll().size();
        final long completed = taskService.countCompleted();
        return Map.of(
                "total", total,
                "completed", completed,
                "pending", total - completed
        );
    }
}
