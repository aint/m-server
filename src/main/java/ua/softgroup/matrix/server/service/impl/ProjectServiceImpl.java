package ua.softgroup.matrix.server.service.impl;

import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ua.softgroup.matrix.server.desktop.model.datamodels.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.TimeModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.service.WorkTimePeriodService;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;
import ua.softgroup.matrix.server.supervisor.consumer.json.ActiveProjectsJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrenciesJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrencyJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.ProjectJson;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl extends AbstractEntityTransactionalService<Project> implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final SupervisorEndpoint supervisorEndpoint;
    private final UserService userService;
    private final WorkDayService workDayService;
    private final WorkTimePeriodService workTimePeriodService;
    private final CacheManager cacheManager;

    private Map<Integer, String> currencyMap = new HashMap<>();
    private Cache currencyCache;

    @Autowired
    public ProjectServiceImpl(ProjectRepository repository, SupervisorEndpoint supervisorEndpoint,
                              UserService userService, WorkDayService workDayService,
                              WorkTimePeriodService workTimePeriodService, CacheManager cacheManager) {
        super(repository);
        this.supervisorEndpoint = supervisorEndpoint;
        this.userService = userService;
        this.workDayService = workDayService;
        this.workTimePeriodService = workTimePeriodService;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void initialize() {
        currencyCache = cacheManager.getCache("currency");
    }

    private void queryCurrencies(String token) throws IOException {
        Response<CurrenciesJson> response = supervisorEndpoint
                .getCurrencies(token)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }

        currencyMap = response.body().getList().stream()
                .map(currencyJson -> { currencyCache.put(currencyJson.getId(), currencyJson.getName()); return currencyJson; })
                .collect(Collectors.toMap(CurrencyJson::getId, CurrencyJson::getName));
    }

    private ActiveProjectsJson queryUserActiveProjects(String token) throws IOException {
        Response<ActiveProjectsJson> response = supervisorEndpoint
                .getUserActiveProjects(token)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }
        return response.body();
    }

    @Override
    public void saveStartWorkTime(String userToken, Long projectId) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = getById(projectId).orElseThrow(NoSuchElementException::new);

        LOG.info("Start work of user {} on project {} at {}", user.getUsername(), projectId, LocalDateTime.now());

        project.setWorkStarted(LocalDateTime.now());
        project.setCheckpointTime(null);
        project.setEndDate(null);
        getRepository().save(project);

        workDayService.save(workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now())
                                          .orElseGet(() -> new WorkDay(user, project, LocalDate.now())));
        //TODO maybe create new work period
    }

    @Override
    public void saveEndWorkTime(String userToken, Long projectId) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = getById(projectId).orElseThrow(NoSuchElementException::new);

        LOG.info("End work of user {} on project {} at {}", user.getUsername(), projectId, LocalDateTime.now());

        LocalDateTime startedWork = Optional.ofNullable(project.getCheckpointTime()).orElse(project.getWorkStarted());

        int seconds = (int) Duration.between(startedWork, LocalDateTime.now()).toMillis() / 1000;
        LOG.debug("User {} worked {} seconds on project {}", user.getUsername(), seconds, project.getId());
        project.setWorkStarted(null);
        project.setCheckpointTime(null);
        //TODO retrieve today work time from WorkDay entity
        project.setTotalSeconds(project.getTotalSeconds() + seconds);
        project.setTodaySeconds(project.getTodaySeconds() + seconds);
        save(project);

        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now())
                                        .orElseGet(() -> new WorkDay(user, project, LocalDate.now()));
        workDay.setWorkSeconds(workDay.getWorkSeconds() + seconds);
        //TODO add rate and currency
        workDayService.save(workDay);

        workTimePeriodService.save(new WorkTimePeriod(startedWork, LocalDateTime.now(), workDay));
    }

    @Override
    public TimeModel saveCheckpointTime(String userToken, Long projectId, Integer idleTime) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = getById(projectId).orElseThrow(NoSuchElementException::new);

        LOG.info("Checkpoint of user {} on project {} at {}", user.getUsername(), projectId, LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime previousCheckpoint = Optional.ofNullable(project.getCheckpointTime()).orElse(project.getWorkStarted());
        int seconds = (int) Duration.between(previousCheckpoint, now).toMillis() / 1000;
        LOG.debug("User {} worked {} seconds and idle {} seconds on project {}", user.getUsername(), seconds, idleTime, project.getId());

        project.setTodaySeconds(project.getTodaySeconds() + seconds);
        project.setTotalSeconds(project.getTotalSeconds() + seconds);
        project.setIdleSeconds(project.getIdleSeconds() + idleTime);
        project.setCheckpointTime(now);
        save(project);

        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now())
                                        .orElseGet(() -> new WorkDay(user, project, LocalDate.now()));
        workDay.setWorkSeconds(workDay.getWorkSeconds() + seconds);
        workDay.setIdleSeconds(workDay.getIdleSeconds() + idleTime);
        //TODO add rate and currency
        workDayService.save(workDay);


        double downtimePercent = Math.floor(project.getIdleSeconds() * 100 / Double.valueOf(project.getTotalSeconds()) * 100) / 100;
        return new TimeModel(project.getTotalSeconds(), project.getTodaySeconds(), downtimePercent);
    }


    @Override
    public Set<ProjectModel> getUserActiveProjects(String token) {
        User user = userService.getByTrackerToken(token).orElseThrow(NoSuchElementException::new);
        Stream<Project> projectStream;
        try {
            queryCurrencies(token);
            projectStream = queryUserActiveProjects(token).getList().stream()
                    .map(project -> addUserAndSaveProject(project, user));
        } catch (IOException e) {
            LOG.info("Failed to query get-user-active-projects {}", e);

            Ehcache nativeCache = (Ehcache) currencyCache.getNativeCache();
            LOG.warn("NativeCache size {}", nativeCache.getSize());
            LOG.warn("NativeCache get {}", nativeCache.getKeys());

            projectStream = getRepository().findByUser(user).stream();
        }
        return projectStream
                .filter(project -> project.getEndDate() == null || LocalDate.now().isBefore(project.getEndDate()))
                .peek(project ->  LOG.debug("User {}, {}", user.getUsername(), project))
                .map(this::convertProjectEntityToModel)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Optional<Project> getBySupervisorIdAndUser(Long supervisorId, User user) {
        return Optional.ofNullable(getRepository().findBySupervisorIdAndUser(supervisorId, user));
    }

    private Project addUserAndSaveProject(ProjectJson projectJson, User user) {
        Project project = getBySupervisorIdAndUser(projectJson.getId(), user).orElseGet(Project::new);
        project.setSupervisorId(projectJson.getId());
        project.setAuthorName(projectJson.getAuthorName());
        project.setDescription(projectJson.getDescription());
        project.setTitle(projectJson.getTitle());
        project.setStartDate(projectJson.getStartDate());
        project.setEndDate(projectJson.getEndDate());
        project.setRate(projectJson.getRate());
        project.setRateCurrencyId(projectJson.getRateCurrencyId());
        project.setUser(user);
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
        double downtimePercent = Math.floor(project.getIdleSeconds() * 100 / Double.valueOf(project.getTotalSeconds()) * 100) / 100;
        projectModel.setProjectTime(new TimeModel(project.getTotalSeconds(), project.getTodaySeconds(), downtimePercent));

        return projectModel;
    }

    @Override
    protected ProjectRepository getRepository() {
        return (ProjectRepository) repository;
    }
}
