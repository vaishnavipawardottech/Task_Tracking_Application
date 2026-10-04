package com.vaishnavipawar.tasktrack.services;

import com.vaishnavipawar.tasktrack.entities.Project;
import com.vaishnavipawar.tasktrack.entities.Task;
import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.repositories.TaskRepository;
import com.vaishnavipawar.tasktrack.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final CurrentUserService currentUserService;
    private final ActivityLogService activityLogService;

    public Task createTask(
            Long projectId,
            String title,
            String description,
            Task.Priority priority,
            LocalDate dueDate,
            Long assignedTo
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireOwner(
                projectId,
                currentUser.getId()
        );

        Project project =
                projectService.getProjectById(projectId);

        User assignedUser = null;

        if (assignedTo != null) {

            assignedUser =
                    userRepository.findById(assignedTo)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Assigned user not found"
                                    )
                            );

            projectService.requireMember(
                    projectId,
                    assignedTo
            );
        }

        Task task = Task.builder()
                .title(title)
                .description(description)
                .status(Task.Status.TODO)
                .priority(
                        priority == null
                                ? Task.Priority.MEDIUM
                                : priority
                )
                .project(project)
                .assignedTo(assignedUser)
                .createdBy(currentUser)
                .dueDate(dueDate)
                .build();

        Task savedTask =
                taskRepository.save(task);

        activityLogService.log(
                currentUser,
                project,
                savedTask,
                "TASK_CREATED",
                "Task '" + savedTask.getTitle()
                        + "' created by "
                        + currentUser.getName()
        );

        if (assignedUser != null) {

            activityLogService.log(
                    currentUser,
                    project,
                    savedTask,
                    "TASK_ASSIGNED",
                    "Task assigned to "
                            + assignedUser.getName()
            );
        }

        return savedTask;
    }

    public List<Task> getTasks(Long projectId) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireMember(
                projectId,
                currentUser.getId()
        );

        return taskRepository
                .findAll()
                .stream()
                .filter(task ->
                        task.getProject()
                                .getId()
                                .equals(projectId))
                .toList();
    }

    public Task getTask(
            Long projectId,
            Long taskId
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireMember(
                projectId,
                currentUser.getId()
        );

        return getTaskById(projectId, taskId);
    }

    public Task updateTask(
            Long projectId,
            Long taskId,
            String title,
            String description,
            Task.Priority priority,
            LocalDate dueDate,
            Long assignedTo
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireOwner(
                projectId,
                currentUser.getId()
        );

        Task task =
                getTaskById(projectId, taskId);

        if (title != null && !title.isBlank()) {
            task.setTitle(title);
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (priority != null) {
            task.setPriority(priority);
        }

        task.setDueDate(dueDate);

        if (assignedTo != null) {

            User assignedUser =
                    userRepository.findById(assignedTo)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Assigned user not found"
                                    )
                            );

            projectService.requireMember(
                    projectId,
                    assignedTo
            );

            task.setAssignedTo(assignedUser);

            activityLogService.log(
                    currentUser,
                    task.getProject(),
                    task,
                    "TASK_ASSIGNED",
                    "Task assigned to "
                            + assignedUser.getName()
            );
        }

        return taskRepository.save(task);
    }

    public Task updateTaskStatus(
            Long projectId,
            Long taskId,
            Task.Status newStatus
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireMember(
                projectId,
                currentUser.getId()
        );

        Task task =
                getTaskById(projectId, taskId);

        boolean isOwner =
                task.getProject()
                        .getCreatedBy()
                        .getId()
                        .equals(currentUser.getId());

        boolean isAssignedUser =
                task.getAssignedTo() != null
                        &&
                        task.getAssignedTo()
                                .getId()
                                .equals(currentUser.getId());

        if (!isOwner && !isAssignedUser) {

            throw new RuntimeException(
                    "Only assigned user or OWNER can change task status"
            );
        }

        Task.Status oldStatus =
                task.getStatus();

        task.setStatus(newStatus);

        Task savedTask =
                taskRepository.save(task);

        activityLogService.log(
                currentUser,
                task.getProject(),
                task,
                "STATUS_CHANGED",
                "Status changed from "
                        + oldStatus
                        + " to "
                        + newStatus
        );

        return savedTask;
    }

    public Task assignTask(
            Long projectId,
            Long taskId,
            Long userId
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireOwner(
                projectId,
                currentUser.getId()
        );

        Task task =
                getTaskById(projectId, taskId);

        User assignedUser =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        projectService.requireMember(
                projectId,
                userId
        );

        task.setAssignedTo(assignedUser);

        Task savedTask =
                taskRepository.save(task);

        activityLogService.log(
                currentUser,
                task.getProject(),
                task,
                "TASK_ASSIGNED",
                "Task assigned to "
                        + assignedUser.getName()
        );

        return savedTask;
    }

    public void deleteTask(
            Long projectId,
            Long taskId
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        projectService.requireOwner(
                projectId,
                currentUser.getId()
        );

        Task task =
                getTaskById(projectId, taskId);

        String taskTitle =
                task.getTitle();

        activityLogService.log(
                currentUser,
                task.getProject(),
                task,
                "TASK_DELETED",
                "Task '" + taskTitle
                        + "' deleted"
        );

        taskRepository.delete(task);
    }

    public List<Task> getMyTasks() {

        User currentUser =
                currentUserService.getCurrentUser();

        return taskRepository
                .findAll()
                .stream()
                .filter(task ->
                        task.getAssignedTo() != null
                                &&
                                task.getAssignedTo()
                                        .getId()
                                        .equals(currentUser.getId()))
                .toList();
    }

    private Task getTaskById(
            Long projectId,
            Long taskId
    ) {

        return taskRepository.findById(taskId)
                .filter(task ->
                        task.getProject()
                                .getId()
                                .equals(projectId))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Task not found in this project"
                        )
                );
    }

}
