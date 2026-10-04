package com.vaishnavipawar.tasktrack.services;

import com.vaishnavipawar.tasktrack.entities.Project;
import com.vaishnavipawar.tasktrack.entities.ProjectMember;
import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.repositories.ProjectMemberRepository;
import com.vaishnavipawar.tasktrack.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final CurrentUserService currentUserService;

    public ProjectMember addMember(
            Long projectId,
            Long userId,
            ProjectMember.ProjectRole role
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireOwner(
                projectId,
                currentUser.getId()
        );

        Project project =
                projectService.getProjectById(projectId);

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        if (projectService.isMember(
                projectId,
                userId
        )) {

            throw new RuntimeException(
                    "User is already a project member"
            );
        }

        if (role == null) {
            role = ProjectMember.ProjectRole.CONTRIBUTOR;
        }

        if (role == ProjectMember.ProjectRole.OWNER) {

            throw new RuntimeException(
                    "New members can only be CONTRIBUTOR"
            );
        }

        ProjectMember member =
                ProjectMember.builder()
                        .project(project)
                        .user(user)
                        .projectRole(role)
                        .build();

        return projectMemberRepository.save(member);
    }

    public List<ProjectMember> getMembers(
            Long projectId
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireMember(
                projectId,
                currentUser.getId()
        );

        return projectMemberRepository
                .findAll()
                .stream()
                .filter(member ->
                        member.getProject()
                                .getId()
                                .equals(projectId))
                .toList();
    }

    public void removeMember(
            Long projectId,
            Long userId
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireOwner(
                projectId,
                currentUser.getId()
        );

        ProjectMember member =
                projectMemberRepository
                        .findAll()
                        .stream()
                        .filter(item ->
                                item.getProject()
                                        .getId()
                                        .equals(projectId)
                                        &&
                                        item.getUser()
                                                .getId()
                                                .equals(userId))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project member not found"
                                )
                        );

        if (member.getProjectRole()
                == ProjectMember.ProjectRole.OWNER) {

            throw new RuntimeException(
                    "Project OWNER cannot be removed"
            );
        }

        projectMemberRepository.delete(member);
    }
}
