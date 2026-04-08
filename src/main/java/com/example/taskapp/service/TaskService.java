package com.example.taskapp.service;

import com.example.taskapp.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();

    public TaskService() {
        reset();
    }

    public void reset() {
        tasks.clear();
        tasks.add(new Task("Buy groceries", "Milk, eggs, bread"));
        tasks.add(new Task("Read a book", "Finish the Playwright docs"));
        tasks.add(new Task("Write tests", "Practice Playwright end-to-end tests"));
    }

    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    public List<Task> findByFilter(String filter) {
        return switch (filter) {
            case "completed" -> tasks.stream().filter(Task::isCompleted).toList();
            case "pending"   -> tasks.stream().filter(t -> !t.isCompleted()).toList();
            default          -> findAll();
        };
    }

    public Optional<Task> findById(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst();
    }

    public Task getByIdOrThrow(int id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public void create(String title, String description) {
        tasks.add(new Task(title, description));
    }

    public void toggle(int id) {
        findById(id).ifPresent(t -> t.setCompleted(!t.isCompleted()));
    }

    public void delete(int id) {
        tasks.removeIf(t -> t.getId() == id);
    }

    public long countCompleted() {
        return tasks.stream().filter(Task::isCompleted).count();
    }
}
