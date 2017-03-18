package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkTimePeriodRepository;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;

import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Service
public class WorkDayServiceImpl extends AbstractEntityTransactionalService<WorkDay> implements WorkDayService {

    private static final Logger logger = LoggerFactory.getLogger(WorkDayServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final WorkTimePeriodRepository workTimePeriodRepository;
    private final UserService userService;
    private final Validator validator;
    private final Environment environment;

    public WorkDayServiceImpl(CrudRepository<WorkDay, Long> repository,
                              ProjectRepository projectRepository,
                              WorkTimePeriodRepository workTimePeriodRepository,
                              UserService userService,
                              Validator validator,
                              Environment environment) {
        super(repository);
        this.projectRepository = projectRepository;
        this.workTimePeriodRepository = workTimePeriodRepository;
        this.userService = userService;
        this.validator = validator;
        this.environment = environment;
    }

    @Override
    public int getTotalWorkSeconds(User author, Project project) {
        return Optional.ofNullable(getRepository().getTotalWorkSeconds(author.getId(), project.getId()))
                       .orElse(0);
    }

    @Override
    public int getCurrentMonthIdleSeconds(User author, Project project) {
        LocalDate start = LocalDate.now().with(firstDayOfMonth());
        LocalDate end = LocalDate.now().with(lastDayOfMonth());
        return Optional.ofNullable(getRepository().getCurrentMonthIdleSeconds(author.getId(), project.getId(), start, end))
                       .orElse(0);
    }

    @Override
    public Optional<WorkDay> getByAuthorAndProjectAndDate(User author, Project project, LocalDate localDate) {
        return Optional.ofNullable(getRepository().findByAuthorAndProjectAndDate(author, project, localDate));
    }

    @Override
    public Set<WorkDay> getAllWorkDaysOf(User user, Project project) {
        return getRepository().findByAuthorAndProject(user, project);
    }

    @Override
    public Set<WorkDay> getAllWorkDaysOf(User user, LocalDate localDate) {
        return getRepository().findByAuthorAndDate(user, localDate);
    }

    @Override
    public Set<WorkDay> getAllWorkDaysOf(Long projectId, LocalDate date) {
        return getRepository().findByProjectIdAndDate(projectId, date);
    }

    @Override
    public Set<WorkDay> getUserWorkDaysBetween(Long userId, LocalDate from, LocalDate to) {
        return getRepository().findByAuthorIdAndDateBetween(userId, from, to);
    }

    @Override
    public Set<WorkDay> getWorkDaysBetween(LocalDate from, LocalDate to) {
        return getRepository().findByDateBetween(from, to);
    }

    @Override
    public Set<WorkDay> getProjectWorkDaysBetween(Long projectSupervisorId, LocalDate from, LocalDate to) {
        return getRepository().findByProjectSupervisorIdAndDateBetween(projectSupervisorId, from, to);
    }

    @Override
    public Set<ReportModel> getWorkDaysOf(String userToken, Long projectId) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = Optional.ofNullable(projectRepository.findOne(projectId))
                                                               .orElseThrow(NoSuchElementException::new);

        logger.info("Request user's '{}' reports of project '{}'", user.getUsername(), projectId);

        return getRepository().findByAuthorAndProject(user, project).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    //TODO maybe throw exception instead of return status?
    public ResponseStatus saveReportOrUpdate(String userToken, Long projectId, ReportModel reportModel) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = Optional.ofNullable(projectRepository.findOne(projectId))
                                                               .orElseThrow(NoSuchElementException::new);

        WorkDay workDay = reportModel.getId() != 0L
                ? getRepository().findOne(reportModel.getId())
                : Optional.ofNullable(getRepository()
                          .findByAuthorAndProjectAndDate(user, project, reportModel.getDate())) //TODO use id for repo, not objects
                          .orElseThrow(NoSuchElementException::new);

        long days = ChronoUnit.DAYS.between(workDay.getDate(), LocalDate.now());
        long editablePeriod = Long.parseLong(environment.getProperty("report.editable.days"));
        if (days > editablePeriod || workDay.isChecked()) {
            logger.warn("Report {} of user '{}' created {} days ago is expired or checked", workDay.getId(), user.getUsername(), days);
            return ResponseStatus.REPORT_EXPIRED;
        }

//        if (!validator.validate(workDay).isEmpty()) {
//            logger.warn("Report {} validation failure", rm.getId());
//            return ResponseStatus.FAIL;
//        }

        logger.info("Save/update user '{}' report of project '{}'", user.getUsername(), project.getId());

        workDay.setReportText(reportModel.getText());
        workDay.setReportUpdated(LocalDateTime.now());

        getRepository().save(workDay);

        return ResponseStatus.SUCCESS;
    }

    @Override
    public LocalDateTime getStartWorkOf(WorkDay workDay) {
        return Optional.ofNullable(workTimePeriodRepository.findTopByWorkDayOrderByStartAsc(workDay))
                       .orElseGet(WorkTimePeriod::new)
                       .getStart();
    }

    @Override
    public LocalDateTime getEndWorkOf(WorkDay workDay) {
        return Optional.ofNullable(workTimePeriodRepository.findTopByWorkDayOrderByEndDesc(workDay))
                       .orElseGet(WorkTimePeriod::new)
                       .getEnd();
    }

    @Override
    public ReportJson convertEntityToJson(WorkDay workDay) {
        return new ReportJson(
                workDay.getId(),
                workDay.getDate(),
                workDay.getReportUpdated(),
                workDay.getReportText(),
                workDay.getWorkSeconds(),
                workDay.isChecked()
        );
    }

    @Override
    protected WorkDayRepository getRepository() {
        return (WorkDayRepository) repository;
    }

    private ReportModel convertEntityToDto(WorkDay workDay) {
        ReportModel reportModel = new ReportModel();
        reportModel.setId(workDay.getId());
        reportModel.setRate(workDay.getProject().getRate());
        reportModel.setCurrency(workDay.getProject().getRateCurrencyId() == 1 ? "USD" : "UAH");
        reportModel.setCoefficient(workDay.getCoefficient());
        reportModel.setText(workDay.getReportText());
        reportModel.setDate(workDay.getDate());
        reportModel.setChecked(workDay.isChecked());
        reportModel.setWorkTime(workDay.getWorkSeconds());
        return reportModel;
    }

}
