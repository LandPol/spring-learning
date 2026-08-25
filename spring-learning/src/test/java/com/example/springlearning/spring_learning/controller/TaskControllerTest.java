package com.example.springlearning.spring_learning.controller;

import com.example.springlearning.spring_learning.exception.TaskNotFoundException;
import com.example.springlearning.spring_learning.model.Task;
import com.example.springlearning.spring_learning.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
                content().contentType("application/json"),
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
}
