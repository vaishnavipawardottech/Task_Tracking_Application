package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.Task;
import com.vaishnavipawar.tasktrack.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask( @PathVariable Long projectId, @RequestBody CreateTaskRequest request ) {
        Task task = taskService.createTask( projectId, request.title(), request.description(), request.priority(), request.dueDate(), request.assignedTo() );

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks( @PathVariable Long projectId ) {
        return ResponseEntity.ok(
                taskService.getTasks(projectId)
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask( @PathVariable Long projectId, @PathVariable Long taskId ) {
        return ResponseEntity.ok(
                taskService.getTask(projectId, taskId)
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask( @PathVariable Long projectId, @PathVariable Long taskId, @RequestBody UpdateTaskRequest request ) {
        return ResponseEntity.ok(
                taskService.updateTask(
                        projectId,
                        taskId,
                        request.title(),
                        request.description(),
                        request.priority(),
                        request.dueDate(),
                        request.assignedTo()
                )
        );
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus( @PathVariable Long projectId, @PathVariable Long taskId, @RequestBody UpdateStatusRequest request ) {
        return ResponseEntity.ok(
                taskService.updateTaskStatus(
                        projectId,
                        taskId,
                        request.status()
                )
        );
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<Task> assignTask( @PathVariable Long projectId, @PathVariable Long taskId, @RequestBody AssignTaskRequest request ) {
        return ResponseEntity.ok(
                taskService.assignTask(
                        projectId,
                        taskId,
                        request.userId()
                )
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask( @PathVariable Long projectId, @PathVariable Long taskId ) {
        taskService.deleteTask(projectId, taskId);

        return ResponseEntity.ok("Task deleted successfully");
    }

    public record CreateTaskRequest(String title, String description, Task.Priority priority, LocalDate dueDate, Long assignedTo) {

    }

    public record UpdateTaskRequest( String title, String description, Task.Priority priority, LocalDate dueDate, Long assignedTo ) {

    }

    public record UpdateStatusRequest( Task.Status status ) {

    }

    public record AssignTaskRequest( Long userId ) {

    }
}

