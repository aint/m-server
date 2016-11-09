package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class ReportServiceImpl extends AbstractEntityTransactionalService<Report> implements ReportService {

    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ReportServiceImpl(ReportRepository repository, ProjectService projectService, UserService userService) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
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
    public Report save(ReportModel rm) throws NoSuchElementException {
        User user = userService.getByTrackerToken(rm.getToken()).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(rm.getProjectId()).orElseThrow(NoSuchElementException::new);
        Report report = Optional.ofNullable(getRepository().findOne(rm.getId())).orElse(new Report(rm.getId()));
        report.setAuthor(user);
        report.setProject(project);
        report.setTitle(rm.getTitle());
        report.setDescription(rm.getDescription());
        return getRepository().save(report);
    }

    @Override
    protected ReportRepository getRepository() {
        return (ReportRepository) repository;
    }
}
