package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.ProjectMember;
import com.vaishnavipawar.tasktrack.services.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ProjectMember> addMember(@PathVariable Long projectId, @RequestBody AddMembershipRequest request) {
        ProjectMember member = projectMemberService.addMember(
                projectId,
                request.userId(),
                request.projectRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @GetMapping
    public ResponseEntity<List<ProjectMember>> getMembers( @PathVariable Long projectId ) {
        return ResponseEntity.ok(
                projectMemberService.getMembers(projectId)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeMember( @PathVariable Long projectId, @PathVariable Long userId ) {
        projectMemberService.removeMember(
                projectId,
                userId
        );

        return ResponseEntity.ok("Member removed successfully");
    }

    public record AddMembershipRequest( Long userId, ProjectMember.ProjectRole projectRole ) {

    }
}
