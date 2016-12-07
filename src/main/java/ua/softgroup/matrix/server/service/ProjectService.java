package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.persistent.entity.Project;

import java.util.Set;

public interface ProjectService extends GeneralEntityService<Project> {

    Set<ProjectModel> getUserActiveProjects(String token);

}
