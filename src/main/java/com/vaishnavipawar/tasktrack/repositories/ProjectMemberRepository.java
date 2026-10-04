package com.vaishnavipawar.tasktrack.repositories;

import com.vaishnavipawar.tasktrack.entities.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
}
