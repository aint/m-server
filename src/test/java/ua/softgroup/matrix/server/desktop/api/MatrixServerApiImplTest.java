package ua.softgroup.matrix.server.desktop.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MatrixServerApiImplTest {

    private static final String TOKEN = "token_value";
    private static final Long PROJECT_SUP_ID = 42L;

    @MockBean
    private UserService userService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private ReportService reportService;

    private MatrixServerApi matrixServerApi;

    private User user;
    private Project project;

    @Before
    public void setUp() throws Exception {
        user = createUser();
        project = createProject();
        when(userService.getByTrackerToken(TOKEN)).thenReturn(Optional.of(user));
        when(projectService.getById(PROJECT_SUP_ID)).thenReturn(Optional.of(project));

        matrixServerApi = new MatrixServerApiImpl(userService, reportService, projectService, null, null, null, null, null, null);
    }

    @Test
    public void saveReport() {
        Report report1 = new Report("report1", "desc1", project, user);
        report1.setCreationDate(LocalDateTime.now().minusDays(1));
        Report report2 = new Report("report2", "desc2", project, user);
        report2.setCreationDate(LocalDateTime.now().minusDays(2));
        Set<Report> reports = generateReportsSetOf(report1, report2);
        when(reportService.getAllReportsOf(user, project)).thenReturn(reports);
        when(reportService.getById(0L)).thenReturn(Optional.empty());

        ReportModel reportModel = getReportModel();
        assertThat(matrixServerApi.saveReport(reportModel)).isEqualTo(Constants.TOKEN_VALIDATED);
        verify(reportService, times(1)).save(reportModel);
    }

    @Test
    public void saveReport_reportExist() {
        Report report = new Report("report1", "desc1", project, user);
        report.setCreationDate(LocalDateTime.now());
        when(reportService.getAllReportsOf(user, project)).thenReturn(generateReportsSetOf(report));

        ReportModel reportModel = getReportModel();
        assertThat(matrixServerApi.saveReport(reportModel)).isEqualTo(Constants.REPORT_EXISTS);
        verify(reportService, never()).save(reportModel);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user-1");
        user.setTrackerToken(TOKEN);
        return user;
    }

    private Project createProject() {
        Project project = new Project();
        project.setSupervisorId(PROJECT_SUP_ID);
        project.setTitle("title");
        return project;
    }

    private ReportModel getReportModel() {
        return new ReportModel(0L, TOKEN, "rmTitle", "rmDesc", PROJECT_SUP_ID);
    }

    private Set<Report> generateReportsSetOf(Report... reports) {
        return Stream.of(reports).collect(Collectors.toSet());
    }

}