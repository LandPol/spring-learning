package com.example.springlearning.spring_learning.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CreateTaskRequestTest {
    @Autowired
    Validator validator;

    @Test
    void shouldHaveNoViolationsWhenCreateTaskRequestIsValid() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "Description 1", 3);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldReturnViolationWhenTitleIsNull() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest(null, "Description 1", 3);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Title is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenTitleIsBlank() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("", "Description 1", 3);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Title is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenDescriptionIsNull() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", null, 3);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Description is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenDescriptionIsBlank() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "", 3);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Description is mandatory")));
    }

    @Test
    void shouldReturnViolationWhenPriorityIsBiggerThanMax() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "Description 1", 6123);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void shouldReturnViolationWhenPriorityIsLowerThanMin() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "Description 1", -6123);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsEqualToMax() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "Description 1", 5);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsEqualToMin() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "Description 1", 0);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsNull() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("Title 1", "Description 1", null);
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(createTaskRequest);
        assertTrue(violations.isEmpty());
    }
}
