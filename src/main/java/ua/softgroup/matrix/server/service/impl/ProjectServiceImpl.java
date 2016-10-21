package ua.softgroup.matrix.server.service.impl;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.service.ProjectService;

import java.util.HashSet;
import java.util.Set;

public class ProjectServiceImpl extends AbstractEntityTransactionalService<Project> implements ProjectService {

    public ProjectServiceImpl() {
        repository = applicationContext.getBean(ProjectRepository.class);
    }

    @Override
    public Set<Project> getAllProjectsOf(User user) {
        //TODO update or delete this
        Set<Project> entities = new HashSet<>();
        getRepository().findAll().forEach(entities::add);
        return entities;
    }

    @Override
    public Set<Report> getAllReports() {
//        getRepository.find
        return null;
    }

    @Override
    protected ProjectRepository getRepository() {
        return (ProjectRepository) repository;
    }
}
