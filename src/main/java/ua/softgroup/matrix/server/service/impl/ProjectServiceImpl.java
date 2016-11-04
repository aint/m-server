package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.UserActiveProjectsResponseModel;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    private UserActiveProjectsResponseModel queryUserActiveProjects(String token) throws IOException {
        Response<UserActiveProjectsResponseModel> response = SupervisorQueriesSingleton.getInstance()
                .getSupervisorQueries()
                .getUserActiveProjects(token)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }
        return response.body();
    }

    @Override
    public Set<Project> getUserActiveProjects(String token) {
        UserActiveProjectsResponseModel userActiveProjectsResponseModel = null;
        try {
            userActiveProjectsResponseModel = queryUserActiveProjects(token);
        } catch (IOException e) {
            //TODO read projects from db
            LOG.error("Failed to query get-user-active-projects {}", e);
        }
        LOG.debug("getUserActiveProjects {}", userActiveProjectsResponseModel);

        return userActiveProjectsResponseModel.getProjectModelList().stream()
                .map(project -> getRepository().save(project))
                .filter(project -> LocalDate.now().isBefore(project.getEndDate()))
                .peek(project -> LOG.warn("pick: {}", project))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public static void main(String[] args) {
        LocalDate end = LocalDate.of(2016, 1, 30);
        System.out.println(end.isBefore(LocalDate.now()));
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
