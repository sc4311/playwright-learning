package com.example.taskapp.controller;

import com.example.taskapp.model.Task;
import com.example.taskapp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String home(Model model) {
        long completed = taskService.countCompleted();
        long total = taskService.findAll().size();
        model.addAttribute("totalTasks", total);
        model.addAttribute("completedTasks", completed);
        model.addAttribute("pendingTasks", total - completed);
        return "home";
    }

    @GetMapping("/tasks")
    public String listTasks(@RequestParam(defaultValue = "all") String filter, Model model) {
        model.addAttribute("tasks", taskService.findByFilter(filter));
        model.addAttribute("filter", filter);
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @PostMapping("/tasks")
    public String createTask(@Valid @ModelAttribute Task task, BindingResult result) {
        if (result.hasErrors()) {
            return "task-form";
        }
        taskService.create(task.getTitle(), task.getDescription());
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/toggle")
    public String toggleTask(@PathVariable int id) {
        taskService.toggle(id);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable int id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}")
    public String viewTask(@PathVariable int id, Model model) {
        model.addAttribute("task", taskService.getByIdOrThrow(id));
        return "task-detail";
    }
}
