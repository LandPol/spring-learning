package com.example.springlearning.spring_learning.service;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.dto.PatchTaskRequest;
import com.example.springlearning.spring_learning.dto.UpdateTaskRequest;
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

    @Test
    void shouldDeleteTaskWhenTaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTaskById(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskCannotBeDeleted() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(1L));

        assertEquals("Task not found.", exception.getMessage());
        verify(taskRepository, never()).deleteById(1L);
    }

    @Test
    void shouldUpdateTaskWhenTaskExistsAndTitleIsUnique() {
        when(taskRepository.existsByTitleAndIdNot("Title 1",1L)).thenReturn(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task(1L, "Task 1", "Description 1", 3)));

        taskService.updateTaskById(1L, new UpdateTaskRequest("Title 2", "Description 2", 1));

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskCannotBeUpdatedBecauseTitleIsNotUnique() {
        when(taskRepository.existsByTitleAndIdNot("Title 1",1L)).thenReturn(true);

        TaskAlreadyExistsException exception = assertThrows(TaskAlreadyExistsException.class, () -> taskService.updateTaskById(1L, new UpdateTaskRequest("Title 2", "Description 2", 1)));

        assertEquals("Task already exists", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskCannotBeUpdatedBecauseTaskDoesNotExist() {
        when(taskRepository.existsByTitleAndIdNot("Title 1",1L)).thenReturn(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskById(1L, new UpdateTaskRequest("Title 2", "Description 2", 1)));

        assertEquals("Task not found.", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldPatchTaskWhenTaskExistsAndTitleIsUnique() {
        when(taskRepository.existsByTitleAndIdNot("Title 1",1L)).thenReturn(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task(1L, "Task 1", "Description 1", 3)));

        taskService.patchTaskById(1L, new PatchTaskRequest(null, "Description 2", 1));

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskCannotBePatchedBecauseTitleIsNotUnique() {
        when(taskRepository.existsByTitleAndIdNot("Title 1",1L)).thenReturn(true);

        TaskAlreadyExistsException exception = assertThrows(TaskAlreadyExistsException.class, () -> taskService.patchTaskById(1L, new PatchTaskRequest(null, "Description 2", 1)));

        assertEquals("Task already exists", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskCannotBePatchedBecauseTaskDoesNotExist() {
        when(taskRepository.existsByTitleAndIdNot("Title 1",1L)).thenReturn(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.patchTaskById(1L, new PatchTaskRequest(null, "Description 2", 1)));

        assertEquals("Task not found.", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }
}
