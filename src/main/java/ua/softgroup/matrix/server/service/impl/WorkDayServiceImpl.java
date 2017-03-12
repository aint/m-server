package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
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

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Service
public class WorkDayServiceImpl extends AbstractEntityTransactionalService<WorkDay> implements WorkDayService {

    private static final Logger logger = LoggerFactory.getLogger(WorkDayServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final Validator validator;
    private final Environment environment;

    public WorkDayServiceImpl(CrudRepository<WorkDay, Long> repository,
                              ProjectRepository projectRepository,
                              UserService userService,
                              Validator validator,
                              Environment environment) {
        super(repository);
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.validator = validator;
        this.environment = environment;
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
    @Transactional
    //TODO check is report editable via repository
    //TODO maybe throw exception instead of return status?
    public ResponseStatus saveReportOrUpdate(String userToken, ReportModel reportModel) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = Optional.ofNullable(projectRepository.findOne(reportModel.getProjectId()))
                                                               .orElseThrow(NoSuchElementException::new);

        //TODO use id for repo, not objects
        WorkDay workDay = Optional.ofNullable(getRepository().findByAuthorAndProjectAndDate(user, project, reportModel.getDate()))
                                                             .orElseThrow(NoSuchElementException::new);

        long hours = Duration.between(workDay.getDate(), LocalDate.now()).toHours();
        long editablePeriod = Long.parseLong(environment.getProperty("report.editable.days")) * 24;
        if (hours > editablePeriod || workDay.isChecked()) {
            logger.warn("Report {} created {} hours ago is expired or checked", workDay.getId(), hours);
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
    @Transactional
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
        reportModel.setProjectId(workDay.getProject().getId()); // TODO it really needed
        reportModel.setText(workDay.getReportText());
        reportModel.setDate(workDay.getDate());
        reportModel.setChecked(workDay.isChecked());
        reportModel.setWorkTime(workDay.getWorkSeconds());
        return reportModel;
    }

}
