package com.example.springlearning.spring_learning.controller;

import com.example.springlearning.spring_learning.model.Task;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldAddNewTask() throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(get("/tasks/{id}", resultTask.getId())).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );
    }

    @Test
    void shouldReturnTaskList() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task 1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task 1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );

        mockMvc.perform(get("/tasks")).andExpectAll(
                status().isOk(),
                jsonPath("$").isArray(),
                jsonPath("$[*].title").value(hasItem("Task 1")),
                jsonPath("$[*].description").value(hasItem("Description 1")),
                jsonPath("$[*].priority").value(hasItem(3))
        );
    }

    @Test
    void shouldReturnConflictWhenTaskAlreadyExists() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 2", "priority": 1}
                        """)
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        mockMvc.perform(get("/tasks/{id}", -1)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundAfterTaskIsDeleted() throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(delete("/tasks/{id}", resultTask.getId())).andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/{id}", resultTask.getId())).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkAfterTaskIsUpdated() throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(put("/tasks/{id}", resultTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 312321", "priority": 2}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 312321"),
                jsonPath("$.priority").value(2)
        );
    }

    @Test
    void shouldReturnNotFoundWhenUpdateTaskDoesNotExist() throws Exception {
        mockMvc.perform(put("/tasks/{id}", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 312321", "priority": 2}
                        """)
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestIsInvalid() throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(put("/tasks/{id}", resultTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": null, "priority": 2}
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenUpdateTaskTitleAlreadyExist() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );

        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test2", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test2"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(put("/tasks/{id}", resultTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 13123", "priority": 2}
                        """)
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldReturnOkAfterTaskIsPatched() throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(patch("/tasks/{id}", resultTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": null, "description": "Description 312321", "priority": 2}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 312321"),
                jsonPath("$.priority").value(2)
        );
    }

    @Test
    void shouldReturnNotFoundWhenPatchTaskDoesNotExist() throws Exception {
        mockMvc.perform(patch("/tasks/{id}", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 312321", "priority": 2}
                        """)
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenPatchTaskRequestIsInvalid() throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(patch("/tasks/{id}", resultTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": null, "priority": 342}
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenPatchTaskTitleAlreadyExist() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test1"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        );

        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test2", "description": "Description 1", "priority": 3}
                        """)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.title").value("Task Integration Test2"),
                jsonPath("$.description").value("Description 1"),
                jsonPath("$.priority").value(3)
        ).andReturn();

        Task resultTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(patch("/tasks/{id}", resultTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Task Integration Test1", "description": null, "priority": 2}
                        """)
        ).andExpect(status().isConflict());
    }
}
