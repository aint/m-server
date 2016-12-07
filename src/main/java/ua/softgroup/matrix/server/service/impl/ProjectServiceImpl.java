package ua.softgroup.matrix.server.service.impl;

import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Response;
import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrenciesResponseModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrencyModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.UserActiveProjectsResponseModel;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl extends AbstractEntityTransactionalService<Project> implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final SupervisorEndpoint supervisorEndpoint;
    private final UserService userService;
    private final WorkTimeService workTimeService;
    private final CacheManager cacheManager;

    private Map<Integer, String> currencyMap = new HashMap<>();
    private Cache currencyCache;

    @Autowired
    public ProjectServiceImpl(ProjectRepository repository, SupervisorEndpoint supervisorEndpoint, UserService userService, WorkTimeService workTimeService, CacheManager cacheManager) {
        super(repository);
        this.supervisorEndpoint = supervisorEndpoint;
        this.userService = userService;
        this.workTimeService = workTimeService;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void initialize() {
        currencyCache = cacheManager.getCache("currency");
    }

    private void queryCurrencies(String token) throws IOException {
        Response<CurrenciesResponseModel> response = supervisorEndpoint
                .getCurrencies(token)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }

        currencyMap = response.body().getCurrencyModelList().stream()
                .map(currencyModel -> {currencyCache.put(currencyModel.getId(), currencyModel.getName()); return currencyModel; })
                .collect(Collectors.toMap(CurrencyModel::getId, CurrencyModel::getName));
    }

    private UserActiveProjectsResponseModel queryUserActiveProjects(String token) throws IOException {
        Response<UserActiveProjectsResponseModel> response = supervisorEndpoint
                .getUserActiveProjects(token)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }
        return response.body();
    }

    @Transactional
    @Override
    public Set<ProjectModel> getUserActiveProjects(String token) {
        User user = userService.getByTrackerToken(token).orElseThrow(NoSuchElementException::new);
        Stream<Project> projectStream;
        try {
            queryCurrencies(token);
            projectStream = queryUserActiveProjects(token).getProjectModelList().stream()
                    .map(project -> addUserAndSaveProject(project, user));
        } catch (IOException e) {
            LOG.info("Failed to query get-user-active-projects {}", e);

            Ehcache nativeCache = (Ehcache) currencyCache.getNativeCache();
            LOG.warn("NativeCache size {}", nativeCache.getSize());
            LOG.warn("NativeCache get {}", nativeCache.getKeys());

            projectStream = user.getProjects().stream()
                    .map(project -> updateProjectRateFromWorkTime(user, project));
        }
        return projectStream
                .filter(project -> project.getEndDate() == null || LocalDate.now().isBefore(project.getEndDate()))
                .peek(project ->  LOG.debug("User {}, {}", user.getUsername(), project))
                .map(project -> setWorkTimeRateFromProject(user, project))
                .map(this::convertProjectEntityToModel)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Project addUserAndSaveProject(Project project, User user) {
        project.getUsers().add(user);
        return getRepository().save(project);
    }

    private ProjectModel convertProjectEntityToModel(Project project) {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setId(project.getId());
        projectModel.setTitle(project.getTitle());
        projectModel.setDescription(project.getDescription());
        projectModel.setAuthorName(project.getAuthorName());
        projectModel.setStartDate(project.getStartDate());
        projectModel.setEndDate(project.getEndDate());
        projectModel.setRate(project.getRate());
        projectModel.setRateCurrency(currencyMap.get(project.getRateCurrencyId()));
        return projectModel;
    }

    private Project setWorkTimeRateFromProject(User user, Project project) {
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(project, user));
        workTime.setRate(project.getRate());
        workTime.setRateCurrencyId(project.getRateCurrencyId());
        workTimeService.save(workTime);
        return project;
    }

    private Project updateProjectRateFromWorkTime(User user, Project project) {
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(project, user));
        project.setRate(workTime.getRate());
        project.setRateCurrencyId(workTime.getRateCurrencyId());
        return project;
    }

    @Override
    protected ProjectRepository getRepository() {
        return (ProjectRepository) repository;
    }
}
