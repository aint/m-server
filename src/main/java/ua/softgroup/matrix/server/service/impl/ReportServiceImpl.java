package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class ReportServiceImpl extends AbstractEntityTransactionalService<Report> implements ReportService {

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkTimeService workTimeService;
    private final WorkDayRepository workDayRepository;
    private final Validator validator;

    @Autowired
    public ReportServiceImpl(ReportRepository repository, ProjectService projectService, UserService userService, WorkTimeService workTimeService, WorkDayRepository workDayRepository, Validator validator) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
        this.workTimeService = workTimeService;
        this.workDayRepository = workDayRepository;
        this.validator = validator;
    }

    @Override
    public Set<Report> getAllReportsOf(User user) {
        return getRepository().findByAuthor(user);
    }

    @Override
    public Set<Report> getAllReportsOf(User user, Project project) {
        return getRepository().findByAuthorAndProject(user, project);
    }

    @Override
    public Set<Report> getTodayReportsOf(User author, Project project) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return getRepository().findByAuthorAndProjectAndCreationDateBetween(author, project, start, end);
    }

    @Override
    public Report save(ReportModel rm) {
        if (!validator.validate(rm).isEmpty()) return null;
        User user = userService.getByTrackerToken(rm.getToken()).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(rm.getProjectId()).orElseThrow(NoSuchElementException::new);
        Report report = Optional.ofNullable(getRepository().findOne(rm.getId())).orElse(new Report(rm.getId()));
        report.setAuthor(user);
        report.setProject(project);
        report.setTitle(rm.getTitle());
        report.setDescription(rm.getDescription());
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NoSuchElementException::new);
        WorkDay workDay = Optional.ofNullable(workDayRepository.findByDateAndWorkTime(report.getCreationDate().toLocalDate(), workTime))
                .orElseGet(() ->  workDayRepository.save(new WorkDay(0L, 0L, workTime)));
        report.setWorkDay(workDay);
        return getRepository().save(report);
    }

    @Override
    @Transactional
    public ReportModel convertEntityToDto(Report report, String token) {
        ReportModel reportModel = new ReportModel();
        reportModel.setToken(token);
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
