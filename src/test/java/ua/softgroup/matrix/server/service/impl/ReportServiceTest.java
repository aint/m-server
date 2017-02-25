package ua.softgroup.matrix.server.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringRunner.class)
public class ReportServiceTest {

    private static final String TOKEN = "token_value";
    private static final Long PROJECT_ID = 42L;

    @MockBean
    private ReportRepository repository;
    @MockBean
    private UserService userService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private WorkDayRepository workDayRepository;
    @MockBean
    private Validator validator;

    private ReportService reportService;

    private User user = createUser();
    private Project project = createProject();

    @Before
    public void setUp() throws Exception {
        when(userService.getByTrackerToken(TOKEN)).thenReturn(Optional.of(user));
        when(projectService.getById(PROJECT_ID)).thenReturn(Optional.of(project));

        reportService = new ReportServiceImpl(repository, projectService, userService, workDayRepository, validator);
    }

    @Test
    public void ifReportExistForToday_true() throws Exception {
        Set<Report> reports = new HashSet<>();
        LongStream.rangeClosed(0, 10)
                  .forEach(i -> reports.add(createReport(i, LocalDateTime.now().minusDays(i))));

        when(repository.findByAuthorAndProject(user, project)).thenReturn(reports);

        assertThat(reportService.ifReportExistForToday(TOKEN, PROJECT_ID)).isTrue();
    }

    @Test
    public void ifReportExistForToday_false() throws Exception {
        Set<Report> reports = new HashSet<>();
        LongStream.rangeClosed(1, 10)
                  .forEach(i -> reports.add(createReport(i, LocalDateTime.now().minusDays(i))));
        when(repository.findByAuthorAndProject(user, project)).thenReturn(reports);

        assertThat(reportService.ifReportExistForToday(TOKEN, PROJECT_ID)).isFalse();
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user-1");
        user.setTrackerToken(TOKEN);
        return user;
    }

    private Project createProject() {
        Project project = new Project(PROJECT_ID);
        project.setTitle("title");
        return project;
    }

    private Report createReport(Long id, LocalDateTime creationDate) {
        Report report = new Report(id);
        report.setCreationDate(creationDate);
        return report;
    }

}