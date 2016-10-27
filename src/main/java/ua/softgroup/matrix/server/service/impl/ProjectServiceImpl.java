package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.UserActiveProjectsResponseModel;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProjectServiceImpl extends AbstractEntityTransactionalService<Project> implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    public ProjectServiceImpl(ProjectRepository repository) {
        super(repository);
    }

    @Override
    public Set<Project> getAllProjectsOf(User user) {
        //TODO update or delete this
        Set<Project> entities = new HashSet<>();
//        getRepository().findAll().forEach(entities::add);
        return entities;
    }

    private Response<UserActiveProjectsResponseModel> executeQuery(String token) {
        Call<UserActiveProjectsResponseModel> call = SupervisorQueriesSingleton.getInstance().getSupervisorQueries().getUserActiveProjects(token);
        try {
            return call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Project> getUserActiveProjects(String token) {
        Response<UserActiveProjectsResponseModel> response = executeQuery(token);
        if (!response.isSuccessful()) {
            throw new RuntimeException("Forbidden");
        }
        UserActiveProjectsResponseModel body = response.body();
        LOG.debug("getUserActiveProjects {}", body);

        List<Project> projects = body.getProjectModelList();
        projects.forEach(this::saveProject);

        return new HashSet<>(projects);
    }

    private void saveProject(Project projectModel) {
        LOG.debug("ProjectModel {}", projectModel);
//        Project project = getRepository().findOne(projectModel.getId());
        LOG.debug("Project {}", projectModel);
        getRepository().save(projectModel);
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
