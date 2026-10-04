package com.vaishnavipawar.tasktrack.repositories;

import com.vaishnavipawar.tasktrack.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
