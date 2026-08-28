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
public class PatchTaskRequestTest {
    @Autowired
    Validator validator;

    @Test
    void shouldHaveNoViolationsWhenPatchTaskRequestIsValid() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", "Description 1", 3);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveNoViolationsWhenAllFieldsAreNull() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest(null, null, null);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldReturnViolationWhenTitleIsBlank() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("", "Description 1", 3);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldReturnViolationWhenTitleIsWhitespace() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest(" ", "Description 1", 3);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldReturnViolationWhenDescriptionIsBlank() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", "", 3);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldReturnViolationWhenDescriptionIsWhitespace() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", " ", 3);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldReturnViolationWhenPriorityIsBiggerThanMax() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", "Description 1", 313);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void shouldReturnViolationWhenPriorityIsLowerThanMin() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", "Description 1", -313);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsEqualToMax() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", "Description 1", 5);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveNoViolationsWhenPriorityIsEqualToMin() {
        PatchTaskRequest patchTaskRequest = new PatchTaskRequest("Task 1", "Description 1", 0);
        Set<ConstraintViolation<PatchTaskRequest>> violations = validator.validate(patchTaskRequest);
        assertTrue(violations.isEmpty());
    }
}
