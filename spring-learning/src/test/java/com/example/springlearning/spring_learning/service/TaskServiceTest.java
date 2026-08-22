package com.example.springlearning.spring_learning.service;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.exception.TaskAlreadyExistsException;
import com.example.springlearning.spring_learning.exception.TaskNotFoundException;
import com.example.springlearning.spring_learning.model.Task;
import com.example.springlearning.spring_learning.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;

    @Test
    void shouldReturnTaskWhenTaskExists() {
        Optional<Task> expectedTask = Optional.of(new Task(1L, "Task 1", "Description 1", 3));
        when(taskRepository.findById(1L)).thenReturn(expectedTask);

        Task taskResult = taskService.getTaskById(1L);

        assertEquals(expectedTask.get(), taskResult);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));

        assertEquals("Task not found.", exception.getMessage());
    }

    @Test
    void shouldReturnTaskWhenTaskAddedSuccessfully() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Task 1", "Description 1", 3);
        when(taskRepository.existsByTitle(createTaskRequest.getTitle())).thenReturn(false);

        Task task = new Task(1L, "Task 1", "Description 1", 3);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task taskResult = taskService.addNewTask(createTaskRequest);

        verify(taskRepository).save(any(Task.class));
        assertEquals(task, taskResult);
    }

    @Test
    void shouldThrowExceptionWhenTaskAlreadyExists() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Task 1", "Description 1", 3);
        when(taskRepository.existsByTitle(createTaskRequest.getTitle())).thenReturn(true);

        TaskAlreadyExistsException exception = assertThrows(TaskAlreadyExistsException.class, () -> taskService.addNewTask(createTaskRequest));
        verify(taskRepository, never()).save(any(Task.class));
        assertEquals("Task already exists", exception.getMessage());
    }
}
