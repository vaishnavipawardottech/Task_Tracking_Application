package com.vaishnavipawar.tasktrack.services;

import com.vaishnavipawar.tasktrack.entities.Project;
import com.vaishnavipawar.tasktrack.entities.ProjectMember;
import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.repositories.ProjectMemberRepository;
import com.vaishnavipawar.tasktrack.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final CurrentUserService currentUserService;

    public Project createProject(
            String name,
            String description
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException(
                    "Only ADMIN can create projects"
            );
        }

        Project project = Project.builder()
                .name(name)
                .description(description)
                .createdBy(currentUser)
                .build();

        Project savedProject =
                projectRepository.save(project);

        ProjectMember owner =
                ProjectMember.builder()
                        .project(savedProject)
                        .user(currentUser)
                        .projectRole(
                                ProjectMember.ProjectRole.OWNER
                        )
                        .build();

        projectMemberRepository.save(owner);

        return savedProject;
    }

    public List<Project> getProjects() {

        User currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole() == User.Role.ADMIN) {

            return projectRepository
                    .findAll()
                    .stream()
                    .filter(project ->
                            project.getCreatedBy()
                                    .getId()
                                    .equals(currentUser.getId()))
                    .toList();
        }

        return projectMemberRepository
                .findAll()
                .stream()
                .filter(member ->
                        member.getUser()
                                .getId()
                                .equals(currentUser.getId()))
                .map(ProjectMember::getProject)
                .toList();
    }

    public Project getProject(Long projectId) {

        User currentUser =
                currentUserService.getCurrentUser();

        Project project = getProjectById(projectId);

        if (!isMember(projectId, currentUser.getId())) {

            throw new RuntimeException(
                    "You are not a member of this project"
            );
        }

        return project;
    }

    public Project updateProject(
            Long projectId,
            String name,
            String description
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        requireOwner(projectId, currentUser.getId());

        Project project =
                getProjectById(projectId);

        if (name != null && !name.isBlank()) {
            project.setName(name);
        }

        if (description != null) {
            project.setDescription(description);
        }

        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId) {

        User currentUser =
                currentUserService.getCurrentUser();

        Project project =
                getProjectById(projectId);

        boolean isSuperAdmin =
                currentUser.getRole() ==
                        User.Role.SUPER_ADMIN;

        boolean isOwner =
                project.getCreatedBy()
                        .getId()
                        .equals(currentUser.getId());

        if (!isSuperAdmin && !isOwner) {

            throw new RuntimeException(
                    "Only project OWNER or SUPER_ADMIN can delete project"
            );
        }

        projectRepository.delete(project);
    }

    public Project getProjectById(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found"
                        )
                );
    }

    public boolean isMember(
            Long projectId,
            Long userId
    ) {

        return projectMemberRepository
                .findAll()
                .stream()
                .anyMatch(member ->
                        member.getProject()
                                .getId()
                                .equals(projectId)
                                &&
                                member.getUser()
                                        .getId()
                                        .equals(userId)
                );
    }

    public void requireMember(
            Long projectId,
            Long userId
    ) {

        if (!isMember(projectId, userId)) {

            throw new RuntimeException(
                    "User is not a member of this project"
            );
        }
    }

    public void requireOwner(
            Long projectId,
            Long userId
    ) {

        boolean owner =
                projectMemberRepository
                        .findAll()
                        .stream()
                        .anyMatch(member ->
                                member.getProject()
                                        .getId()
                                        .equals(projectId)
                                        &&
                                        member.getUser()
                                                .getId()
                                                .equals(userId)
                                        &&
                                        member.getProjectRole()
                                                == ProjectMember.ProjectRole.OWNER
                        );

        if (!owner) {

            throw new RuntimeException(
                    "Only project OWNER can perform this operation"
            );
        }
    }
}
