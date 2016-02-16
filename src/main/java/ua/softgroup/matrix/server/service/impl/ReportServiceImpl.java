package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.desktop.api.Constants;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
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

@Service
public class ReportServiceImpl extends AbstractEntityTransactionalService<Report> implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayRepository workDayRepository;
    private final Validator validator;

    @Autowired
    public ReportServiceImpl(ReportRepository repository, ProjectService projectService,
                             UserService userService, WorkDayRepository workDayRepository,
                             Validator validator) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
        this.workDayRepository = workDayRepository;
        this.validator = validator;
    }

    @Override
    public Set<Report> getAllReportsOf(User user, Project project) {
        return getRepository().findByAuthorAndProject(user, project);
    }

    @Override
    @Transactional
    public Set<ReportModel> getAllReportsOf(String userToken, Long projectId) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
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
    public Constants saveOrUpdate(ReportModel reportModel, long editablePeriod) {
        Report report = getRepository().findOne(reportModel.getId());
        if (report != null) {
            Duration duration = Duration.between(report.getCreationDate(), LocalDateTime.now());
            logger.debug("Updating report created {} hours ago", duration.toHours());
            if (duration.toHours() > editablePeriod) {
                logger.debug("Report expired");
                return Constants.REPORT_EXPIRED;
            }
        }
        save(reportModel);
        return Constants.TOKEN_VALIDATED;
    }

    private Report save(ReportModel rm) {
        if (!validator.validate(rm).isEmpty()) return null;
        User user = userService.getByTrackerToken(rm.getToken()).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(rm.getProjectId()).orElseThrow(NoSuchElementException::new);
        Report report = Optional.ofNullable(getRepository().findOne(rm.getId())).orElse(new Report(rm.getId()));
        report.setAuthor(user);
        report.setProject(project);
        report.setTitle(rm.getTitle());
        report.setDescription(rm.getDescription());
        LocalDate creationDate = Optional.ofNullable(report.getCreationDate()).orElseGet(LocalDateTime::now).toLocalDate();
        WorkDay workDay = Optional.ofNullable(workDayRepository.findByDateAndProject(creationDate, project))
                .orElseGet(() ->  workDayRepository.save(new WorkDay(0L, 0L, project)));
        report.setWorkDay(workDay);
        return getRepository().save(report);
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

    private ReportModel convertEntityToDto(Report report) {
        ReportModel reportModel = new ReportModel();
        reportModel.setId(report.getId());
        reportModel.setTitle(report.getTitle());
        reportModel.setDescription(report.getDescription());
        reportModel.setProjectId(report.getProject().getId());
        reportModel.setDate(report.getCreationDate().toLocalDate());
        reportModel.setChecked(report.getWorkDay().isChecked());
        return reportModel;
    }

    @Override
    protected ReportRepository getRepository() {
        return (ReportRepository) repository;
    }
}
