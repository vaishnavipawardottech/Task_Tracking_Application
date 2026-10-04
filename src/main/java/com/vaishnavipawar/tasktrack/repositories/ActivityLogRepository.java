package com.vaishnavipawar.tasktrack.repositories;

import com.vaishnavipawar.tasktrack.entities.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByProjectId(
            Long projectId,
            Pageable pageable
    );

    Page<ActivityLog> findByProjectIdAndTaskId(
            Long projectId,
            Long taskId,
            Pageable pageable
    );

    Page<ActivityLog> findByProjectIdAndPerformedById(
            Long projectId,
            Long userId,
            Pageable pageable
    );
}
