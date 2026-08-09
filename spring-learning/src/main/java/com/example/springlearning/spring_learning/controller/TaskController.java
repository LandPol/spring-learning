package com.example.springlearning.spring_learning.controller;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.dto.UpdateTaskRequest;
import com.example.springlearning.spring_learning.model.Task;
import com.example.springlearning.spring_learning.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping("/tasks")
    public Task addNewTask(@RequestBody CreateTaskRequest createTaskRequest) {
        return taskService.addNewTask(createTaskRequest);
    }

    @GetMapping("/tasks/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTaskById(@PathVariable Long id, @RequestBody UpdateTaskRequest updateTaskRequest) {
        return new ResponseEntity<>(taskService.updateTaskById(id, updateTaskRequest), HttpStatus.OK);
    }
}
