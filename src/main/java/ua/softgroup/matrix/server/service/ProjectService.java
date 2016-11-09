package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Set;

public interface ProjectService extends GeneralEntityService<Project> {

    Set<Project> getAllProjectsOf(User user);

    Set<ProjectModel> getUserActiveProjects(String token);

}
