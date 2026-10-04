package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.Task;
import com.vaishnavipawar.tasktrack.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class MyTaskController {
    private final TaskService taskService;

    @GetMapping("/my-tasks")
    public ResponseEntity<List<Task>> getMyTasks() {
        return ResponseEntity.ok(
                taskService.getMyTasks()
        );
    }
}
