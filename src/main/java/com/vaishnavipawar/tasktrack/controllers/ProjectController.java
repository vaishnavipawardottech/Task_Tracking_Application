package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.Project;
import com.vaishnavipawar.tasktrack.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody CreateProjectRequest request) {
        Project project = projectService.createProject(
                request.name(),
                request.description()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(
                projectService.getProjects()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject( @PathVariable Long id ) {
        return ResponseEntity.ok(
                projectService.getProject(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject( @PathVariable Long id, @RequestBody UpdateProjectRequest request ) {
        return ResponseEntity.ok(
                projectService.updateProject(
                        id,
                        request.name(),
                        request.description()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject( @PathVariable Long id) {
        projectService.deleteProject(id);

        return ResponseEntity.ok("Project deleted successfully");
    }

    public record CreateProjectRequest(
            String name,
            String description
    ) {
    }

    public record UpdateProjectRequest(
            String name,
            String description
    ) {
    }
}
