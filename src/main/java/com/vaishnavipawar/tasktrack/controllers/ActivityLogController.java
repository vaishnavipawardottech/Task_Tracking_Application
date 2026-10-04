package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.ActivityLog;
import com.vaishnavipawar.tasktrack.services.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/projects/{projectId}/activity")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<Page<ActivityLog>> getActivity(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long userId,
            Pageable pagable
    ) {
        return ResponseEntity.ok(
                activityLogService.getActivity(
                        projectId,
                        taskId,
                        userId,
                        pagable
                )
        );
    }
}
