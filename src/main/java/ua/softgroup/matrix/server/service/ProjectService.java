package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.desktop.model.datamodels.ProjectModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Optional;
import java.util.Set;

public interface ProjectService extends GeneralEntityService<Project> {

    void saveStartWorkTime(String userToken, Long projectId);

    void saveEndWorkTime(String userToken, Long projectId);

    void saveCheckpointTime(Long projectId, Integer idleTime);

    Set<ProjectModel> getUserActiveProjects(String token);

    Optional<Project> getBySupervisorIdAndUser(Long supervisorId, User user);

}
