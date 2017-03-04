package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;

import javax.validation.Validator;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus.REPORT_EXISTS;

@Service
@PropertySource("classpath:desktop.properties")
public class ReportServiceImpl extends AbstractEntityTransactionalService<Report> implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayRepository workDayRepository;
    private final Validator validator;
    private final Environment environment;

    @Autowired
    public ReportServiceImpl(ReportRepository repository, ProjectService projectService,
                             UserService userService, WorkDayRepository workDayRepository,
                             Validator validator, Environment environment) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
        this.workDayRepository = workDayRepository;
        this.validator = validator;
        this.environment = environment;
    }

    @Override
    public Set<Report> getAllReportsOf(User user, Project project) {
        return getRepository().findByAuthorAndProject(user, project);
    }

    @Override
    @Transactional
    public Set<ReportModel> getReportsOf(String userToken, Long projectId) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);

        logger.info("Request user '{}' reports of project '{}'", user.getUsername(), projectId);

        return getRepository().findByAuthorAndProject(user, project).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean ifReportExistForToday(String userToken, Long projectId) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
        return getRepository().findByAuthorAndProject(user, project).stream()
                .map(Report::getCreationDate)
                .anyMatch(creationDate -> LocalDate.now().isEqual(creationDate.toLocalDate()));
    }


    @Override
    @Transactional
    //TODO check is report editable via repository
    //TODO maybe throw exception instead of return status?
    public ResponseStatus saveOrUpdate(String userToken, ReportModel reportModel) {
        if (reportModel.getId() == 0 && ifReportExistForToday(userToken, reportModel.getProjectId())) {
            logger.warn("Report exists and can't be updated without id");
            return REPORT_EXISTS;
        }

        Report report = getRepository().findOne(reportModel.getId());
        if (report != null) {
            long hours = Duration.between(report.getCreationDate(), LocalDateTime.now()).toHours();
            long editablePeriod = Long.parseLong(environment.getProperty("report.editable.days")) * 24;
            if (hours > editablePeriod || report.getWorkDay().isChecked()) {
                logger.warn("Report {} created {} hours ago is expired or checked", report.getId(), hours);
                return ResponseStatus.REPORT_EXPIRED;
            }
        }

        return save(userToken, reportModel);
    }

    private ResponseStatus save(String userToken, ReportModel rm) {
        if (!validator.validate(rm).isEmpty()) {
            logger.warn("Report {} validation failure", rm.getId());
            return ResponseStatus.FAIL;
        }

        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(rm.getProjectId()).orElseThrow(NoSuchElementException::new);

        logger.info("Save/update user '{}' report of project '{}'", user.getUsername(), project.getId());

        Report report = Optional.ofNullable(getRepository().findOne(rm.getId()))
                                .orElse(new Report(rm.getId()));
        report.setAuthor(user);
        report.setProject(project);
        report.setDescription(rm.getText());
        LocalDate creationDate = report.getCreationDate().toLocalDate();
        WorkDay workDay = Optional.ofNullable(workDayRepository.findByDateAndProject(creationDate, project))
                                  .orElseGet(() ->  workDayRepository.save(new WorkDay(0L, 0L, project)));
        report.setWorkDay(workDay);
        getRepository().save(report);

        return ResponseStatus.SUCCESS;
    }

    @Override
    @Transactional
    public ReportJson convertEntityToJson(Report report) {
        return new ReportJson(
                report.getId(),
                report.getCreationDate(),
                report.getUpdateDate(),
                report.getTitle(),
                report.getDescription(),
                report.getWorkDay().getWorkMinutes(),
                report.getWorkDay().isChecked()
        );
    }

    //TODO implement attachment
    private ReportModel convertEntityToDto(Report report) {
        ReportModel reportModel = new ReportModel();
        reportModel.setId(report.getId());
        reportModel.setProjectId(report.getProject().getId());
        reportModel.setText(report.getDescription());
        reportModel.setDate(report.getCreationDate().toLocalDate());
        reportModel.setChecked(report.getWorkDay().isChecked());
        reportModel.setWorkTime(String.valueOf(report.getWorkDay().getWorkMinutes())); //TODO use int value
        return reportModel;
    }

    @Override
    protected ReportRepository getRepository() {
        return (ReportRepository) repository;
    }
}
