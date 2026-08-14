package com.example.springlearning.spring_learning.service;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.dto.PatchTaskRequest;
import com.example.springlearning.spring_learning.dto.UpdateTaskRequest;
import com.example.springlearning.spring_learning.exception.TaskAlreadyExistsException;
import com.example.springlearning.spring_learning.exception.TaskNotFoundException;
import com.example.springlearning.spring_learning.model.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.springlearning.spring_learning.repository.TaskRepository;
import java.util.List;
import java.util.Optional;

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
        if (taskRepository.existsByTitle(createTaskRequest.getTitle())) {
            throw new TaskAlreadyExistsException("Task already exists");
        }
        Task task = new Task(null, createTaskRequest.getTitle(), createTaskRequest.getDescription());
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException("Task not found.");
        }
        return task.get();
    }

    public void deleteTaskById(Long id) {
        boolean result = taskRepository.existsById(id);
        if (!result) {
            throw new TaskNotFoundException("Task not found.");
        }
        taskRepository.deleteById(id);
    }

    public Task updateTaskById(Long id, UpdateTaskRequest updateTaskRequest) {
        if (taskRepository.existsByTitleAndIdNot(updateTaskRequest.getTitle(), id)) {
            throw new TaskAlreadyExistsException("Task already exists");
        }
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException("Task not found.");
        }
        Task taskToUpdate = task.get();
        taskToUpdate.setTitle(updateTaskRequest.getTitle());
        taskToUpdate.setDescription(updateTaskRequest.getDescription());
        taskRepository.save(taskToUpdate);
        return taskToUpdate;
    }

    public Task patchTaskById(Long id, PatchTaskRequest patchTaskRequest) {
        if (patchTaskRequest.getTitle() != null) {
            if (taskRepository.existsByTitleAndIdNot(patchTaskRequest.getTitle(), id)) {
                throw new TaskAlreadyExistsException("Task already exists");
            }
        }

        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException("Task not found.");
        }
        Task taskToPatch = task.get();
        if (patchTaskRequest.getTitle() != null) {
            taskToPatch.setTitle(patchTaskRequest.getTitle());
        }
        if (patchTaskRequest.getDescription() != null) {
            taskToPatch.setDescription(patchTaskRequest.getDescription());
        }
        taskRepository.save(taskToPatch);
        return taskToPatch;
    }
}
