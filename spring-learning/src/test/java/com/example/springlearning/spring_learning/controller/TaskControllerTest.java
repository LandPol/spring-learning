package com.example.springlearning.spring_learning.controller;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.exception.TaskNotFoundException;
import com.example.springlearning.spring_learning.model.Task;
import com.example.springlearning.spring_learning.service.TaskService;
import org.apache.coyote.BadRequestException;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @MockitoBean
    TaskService taskService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnTaskWhenTaskExists() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(new Task(1L, "Task 1", "Description 1", 3));
        mockMvc.perform(get("/tasks/1")).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.title").value("Task 1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );

        verify(taskService).getTaskById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new TaskNotFoundException("Task not found."));
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddNewTaskWhenRequestIsValid() throws Exception {
        when(taskService.addNewTask(any(CreateTaskRequest.class))).thenReturn(new Task(1L, "Task 1", "Description 1", 3));
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.title").value("Task 1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );

        ArgumentCaptor<CreateTaskRequest> createTaskRequestArgumentCaptor = ArgumentCaptor.forClass(CreateTaskRequest.class);
        verify(taskService).addNewTask(createTaskRequestArgumentCaptor.capture());

        CreateTaskRequest capturedCreateTaskRequest = createTaskRequestArgumentCaptor.getValue();
        assertEquals("Task 1", capturedCreateTaskRequest.getTitle());
        assertEquals("Description 1", capturedCreateTaskRequest.getDescription());
        assertEquals(3, capturedCreateTaskRequest.getPriority());
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsInvalid() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "description": "Description 1", "priority": 3}
                        """)
        ).andExpect(status().isBadRequest());

        verify(taskService, never()).addNewTask(any(CreateTaskRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenPriorityIsBiggerThanMax() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": 613}
                        """)
        ).andExpect(status().isBadRequest());

        verify(taskService, never()).addNewTask(any(CreateTaskRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenPriorityIsLessThanMin() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": -123}
                        """)
        ).andExpect(status().isBadRequest());

        verify(taskService, never()).addNewTask(any(CreateTaskRequest.class));
    }

    @Test
    void shouldReturnTaskListWhenTaskExists() throws Exception {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(1L, "Task 1", "Description 1", 3));
        taskList.add(new Task(2L, "Task 2", "Description 2", 1));
        when(taskService.getAllTasks()).thenReturn(taskList);
        mockMvc.perform(get("/tasks")).andExpectAll(
                status().isOk(),
                jsonPath("$[0].title").value("Task 1"),
                jsonPath("$[0].description").value("Description 1"),
                jsonPath("$[0].priority").value(3),

                jsonPath("$[1].title").value("Task 2"),
                jsonPath("$[1].description").value("Description 2"),
                jsonPath("$[1].priority").value(1),

                jsonPath("$.length()").value(2)
        );

        verify(taskService).getAllTasks();
    }

    @Test
    void shouldReturnEmptyTaskListWhenTaskDoesNotExist() throws Exception {
        List<Task> taskList = new ArrayList<>();
        when(taskService.getAllTasks()).thenReturn(taskList);
        mockMvc.perform(get("/tasks")).andExpectAll(
                status().isOk(),
                jsonPath("$.length()").value(0)
        );

        verify(taskService).getAllTasks();
    }
}
