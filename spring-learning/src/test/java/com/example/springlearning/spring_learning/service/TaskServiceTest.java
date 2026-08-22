package com.example.springlearning.spring_learning.service;

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
import static org.mockito.Mockito.when;

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
}
