package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.api.model.datamodels.ProjectModel;
import ua.softgroup.matrix.api.model.datamodels.TimeModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Optional;
import java.util.Set;

public interface ProjectService extends GeneralEntityService<Project> {

    TimeModel saveStartWorkTime(String userToken, Long projectId);

    TimeModel saveEndWorkTime(String userToken, Long projectId);

    TimeModel saveCheckpointTime(String userToken, Long projectId, Integer idleTime);

    Set<ProjectModel> getUserActiveProjects(String token);

    Optional<Project> getBySupervisorIdAndUser(Long supervisorId, User user);

}
