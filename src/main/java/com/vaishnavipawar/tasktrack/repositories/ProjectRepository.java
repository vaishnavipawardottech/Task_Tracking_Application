package com.vaishnavipawar.tasktrack.repositories;

import com.vaishnavipawar.tasktrack.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
