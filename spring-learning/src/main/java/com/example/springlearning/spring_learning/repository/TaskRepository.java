package com.example.springlearning.spring_learning.repository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import com.example.springlearning.spring_learning.model.Task;

@Repository
public class TaskRepository {
    private Task taskA = new Task(1L, "a", "aaa");
    private Task taskB = new Task(2L, "b", "bbb");
    private Task taskC = new Task(3L, "c", "ccc");

    private List<Task> tasks = new ArrayList<>();

    public TaskRepository() {
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
    }


    public List<Task> findAll() {
        return tasks;
    }

    public Task addNewTask(Task task) {
        task.setId(dateTimeToLong());
        tasks.add(task);
        return task;
    }

    private Long dateTimeToLong() {
        LocalDateTime localDateTime = LocalDateTime.now();

        return localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public Task findTaskById(Long id) {
        for(Task task : tasks) {
            if(task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    public boolean deleteTaskById(Long id) {
        Task task = findTaskById(id);
        if (task != null) {
            tasks.remove(task);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateTask(Task task) {
        if (task != null) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(task.getId())) {
                    tasks.set(i, task);
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
