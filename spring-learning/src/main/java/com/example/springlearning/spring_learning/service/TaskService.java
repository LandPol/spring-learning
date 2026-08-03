package com.example.springlearning.spring_learning.service;

import com.example.springlearning.spring_learning.model.Task;
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

    public void addNewTask(Task task) {
        taskRepository.addNewTask(task);
    }
}
