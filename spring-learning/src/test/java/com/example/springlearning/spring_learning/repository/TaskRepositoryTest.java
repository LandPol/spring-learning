package com.example.springlearning.spring_learning.repository;

import com.example.springlearning.spring_learning.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TaskRepositoryTest {
    @Autowired
    TaskRepository taskRepository;

    @Test
    void shouldReturnTrueWhenTaskWithGivenTitleExists() {
        taskRepository.save(new Task(null, "Task 1", "Descriptin 1", 3));

        assertTrue(taskRepository.existsByTitle("Task 1"));
    }

    @Test
    void shouldReturnFalseWhenTaskWithGivenTitleDoesNotExist() {
        assertFalse(taskRepository.existsByTitle("Task 1"));
    }

    @Test
    void shouldReturnFalseWhenTaskWithGivenTitleHasSameId() {
        Task savedTask = taskRepository.save(new Task(null, "Task 1", "Descriptin 1", 3));

        assertFalse(taskRepository.existsByTitleAndIdNot("Task 1", savedTask.getId()));
    }

    @Test
    void shouldReturnTrueWhenTaskWithGivenTitleExistsWithDifferentId() {
        taskRepository.save(new Task(null, "Task 1", "Descriptin 1", 3));
        Task otherTask = taskRepository.save(new Task(null, "Task 2", "Descriptin 2", 1));

        assertTrue(taskRepository.existsByTitleAndIdNot("Task 1", otherTask.getId()));
    }

    @Test
    void shouldReturnFalseWhenTaskWithGivenTitleDoesNotExists() {
        Task savedTask = taskRepository.save(new Task(null, "Task 1", "Descriptin 1", 3));

        assertFalse(taskRepository.existsByTitleAndIdNot("Task 2", savedTask.getId()));
    }
}
