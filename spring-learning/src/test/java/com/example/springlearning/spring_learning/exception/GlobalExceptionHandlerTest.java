package com.example.springlearning.spring_learning.exception;

import com.example.springlearning.spring_learning.dto.CreateTaskRequest;
import com.example.springlearning.spring_learning.dto.PatchTaskRequest;
import com.example.springlearning.spring_learning.model.Task;
import com.example.springlearning.spring_learning.service.TaskService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.empty;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GlobalExceptionHandlerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TaskService taskService;

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(TaskNotFoundException.class);
        mockMvc.perform(get("/tasks/1")).andExpectAll(
                status().isNotFound(),
                jsonPath("$.status").value(404),
                jsonPath("$.message").value("Task not found"),
                jsonPath("$.errors").value(empty()),
                jsonPath("$.timestamp").exists()
        );

        verify(taskService).getTaskById(1L);
    }

    @Test
    void shouldReturnConflictWhenTaskAlreadyExists() throws Exception {
        when(taskService.addNewTask(any(CreateTaskRequest.class))).thenThrow(TaskAlreadyExistsException.class);
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isConflict(),
                jsonPath("$.status").value(409),
                jsonPath("$.message").value("Task already exists"),
                jsonPath("$.errors").value(empty()),
                jsonPath("$.timestamp").exists()
        );

        verify(taskService).addNewTask(any(CreateTaskRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestTitleIsNull() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": null, "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("title")),
                jsonPath("$.errors[*].message").value(hasItem("Title is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestDescriptionIsNull() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": null, "priority": 3}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("description")),
                jsonPath("$.errors[*].message").value(hasItem("Description is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestTitleIsBlank() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("title")),
                jsonPath("$.errors[*].message").value(hasItem("Title is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestDescriptionIsBlank() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": " ", "priority": 3}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("description")),
                jsonPath("$.errors[*].message").value(hasItem("Description is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestPriorityIsBiggerThanMax() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": 3313}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("priority")),
                jsonPath("$.errors[*].message").value(hasItem("must be less than or equal to 5")),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestPriorityIsLowerThanMin() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": -3313}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("priority")),
                jsonPath("$.errors[*].message").value(hasItem("must be greater than or equal to 0")),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestTitleIsNull() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": null, "description": "Description 2", "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("title")),
                jsonPath("$.errors[*].message").value(hasItem("Title is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestDescriptionIsNull() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": null, "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("description")),
                jsonPath("$.errors[*].message").value(hasItem("Description is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestTitleIsBlank() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "description": "Description 2", "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("title")),
                jsonPath("$.errors[*].message").value(hasItem("Title is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestDescriptionIsBlank() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": " ", "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("description")),
                jsonPath("$.errors[*].message").value(hasItem("Description is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestTitleAndDescriptionAreBlank() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "description": " ", "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("title")),
                jsonPath("$.errors[*].field").value(hasItem("description")),
                jsonPath("$.errors[*].message").value(hasItem("Title is mandatory")),
                jsonPath("$.errors[*].message").value(hasItem("Description is mandatory")),
                jsonPath("$.timestamp").exists()

        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestPriorityIsBiggerThanMax() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": "Description 2", "priority": 3313}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("priority")),
                jsonPath("$.errors[*].message").value(hasItem("must be less than or equal to 5")),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestPriorityIsLowerThanMin() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": "Description 2", "priority": -3313}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("priority")),
                jsonPath("$.errors[*].message").value(hasItem("must be greater than or equal to 0")),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenPatchTaskRequestTitleIsBlank() throws Exception {
        mockMvc.perform(patch("/tasks/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "description": "Description 2", "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("title")),
                jsonPath("$.errors[*].message").exists(),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenPatchTaskRequestDescriptionIsBlank() throws Exception {
        mockMvc.perform(patch("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": " ", "priority": 1}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("description")),
                jsonPath("$.errors[*].message").exists(),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenPatchTaskRequestPriorityIsBiggerThanMax() throws Exception {
        mockMvc.perform(patch("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": "Description 2", "priority": 3313}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("priority")),
                jsonPath("$.errors[*].message").value(hasItem("must be less than or equal to 5")),
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void shouldReturnBadRequestWhenPatchTaskRequestPriorityIsLowerThanMin() throws Exception {
        mockMvc.perform(patch("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 2", "description": "Description 2", "priority": -3313}
                        """)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("Validation failed"),
                jsonPath("$.errors[*].field").value(hasItem("priority")),
                jsonPath("$.errors[*].message").value(hasItem("must be greater than or equal to 0")),
                jsonPath("$.timestamp").exists()
        );
    }
}
