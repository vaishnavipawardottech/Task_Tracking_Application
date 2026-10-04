package com.vaishnavipawar.tasktrack.services;

import com.vaishnavipawar.tasktrack.entities.ActivityLog;
import com.vaishnavipawar.tasktrack.entities.Project;
import com.vaishnavipawar.tasktrack.entities.Task;
import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.repositories.ActivityLogRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final CurrentUserService currentUserService;
    private final ProjectService projectService;

    public void log(
            User performedBy,
            Project project,
            Task task,
            String action,
            String details
    ) {

        ActivityLog log =
                ActivityLog.builder()
                        .performedBy(performedBy)
                        .project(project)
                        .task(task)
                        .action(action)
                        .details(details)
                        .build();

        activityLogRepository.save(log);
    }


    public Page<ActivityLog> getActivity(
            Long projectId,
            Long taskId,
            Long userId,
            Pageable pageable
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireMember(
                projectId,
                currentUser.getId()
        );

        if (userId != null) {

            projectService.requireOwner(
                    projectId,
                    currentUser.getId()
            );
        }

        return activityLogRepository
                .findAll(pageable);
    }
}
