package com.example.springlearning.spring_learning.service;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.exception.TaskNotFoundException;
import com.example.springlearning.spring_learning.model.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.springlearning.spring_learning.repository.TaskRepository;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task addNewTask(CreateTaskRequest createTaskRequest) {
        Task task = new Task(null, createTaskRequest.getTitle(), createTaskRequest.getDescription());
        return taskRepository.addNewTask(task);
    }

    public Task getTaskById(Long id) {
        Task task = taskRepository.findTaskById(id);
        if (task == null) {
            throw new TaskNotFoundException("Task not found.");
        }
        return task;
    }

    public void deleteTaskById(Long id) {
        boolean result = taskRepository.deleteTaskById(id);
        if (!result) {
            throw new TaskNotFoundException("Task not found.");
        }
    }
}
