package com.example.springlearning.spring_learning.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UpdateTaskRequestTest {
    @Autowired
    Validator validator;

    @Test
    void shouldHaveNoViolationsWhenUpdateTaskRequestIsValid() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Task 1", "Description 1", 3);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldReturnViolationWhenTitleIsNull() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(null, "Description 1", 3);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Title is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenTitleIsBlank() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("", "Description 1", 3);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Title is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenDescriptionIsNull() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Title 1", null, 3);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Description is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenDescriptionIsBlank() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Title 1", "", 3);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Description is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenPriorityIsBiggerThanMax() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Title 1", "Description 1", 6123);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void shouldReturnViolationWhenPriorityIsLowerThanMin() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Title 1", "Description 1", -6123);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsEqualMax() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Title 1", "Description 1", 5);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsEqualMin() {
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Title 1", "Description 1", 0);
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(updateTaskRequest);
        assertTrue(violations.isEmpty());
    }
}
