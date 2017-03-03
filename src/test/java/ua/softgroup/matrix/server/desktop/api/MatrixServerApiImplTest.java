package ua.softgroup.matrix.server.desktop.api;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class MatrixServerApiImplTest {
//
//    private static final String TOKEN = "token_value";
//    private static final Long PROJECT_SUP_ID = 42L;
//
//    @MockBean
//    private UserService userService;
//    @MockBean
//    private ProjectService projectService;
//    @MockBean
//    private ReportService reportService;
//    @MockBean
//    private Environment environment;
//
//    private MatrixServerApi matrixServerApi;
//
//    private User user;
//    private Project project;
//
//    @Before
//    public void setUp() throws Exception {
//        user = createUser();
//        project = createProject();
//        when(userService.getByTrackerToken(TOKEN)).thenReturn(Optional.of(user));
//        when(projectService.getById(PROJECT_SUP_ID)).thenReturn(Optional.of(project));
//        when(environment.getProperty("report.editable.days")).thenReturn("2");
//
//        matrixServerApi = new MatrixServerApiImpl(userService, reportService, projectService, null, null, null, null, environment);
//    }
//
//    @Test
//    public void saveReport() {
//        ReportModel reportModel = getReportModel(1L);
//
//        when(reportService.ifReportExistForToday(TOKEN, PROJECT_SUP_ID)).thenReturn(false);
//        when(reportService.saveOrUpdate(reportModel, 48)).thenReturn(Constants.TOKEN_VALIDATED);
//
//        assertThat(matrixServerApi.saveReport(reportModel)).isEqualTo(Constants.TOKEN_VALIDATED);
//    }
//
//    @Test
//    public void saveReport_reportExist() {
//        when(reportService.ifReportExistForToday(TOKEN, PROJECT_SUP_ID)).thenReturn(true);
//
//        ReportModel reportModel = getReportModel(0L);
//        assertThat(matrixServerApi.saveReport(reportModel)).isEqualTo(Constants.REPORT_EXISTS);
//    }
//
//    private User createUser() {
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("user-1");
//        user.setTrackerToken(TOKEN);
//        return user;
//    }
//
//    private Project createProject() {
//        Project project = new Project();
//        project.setSupervisorId(PROJECT_SUP_ID);
//        project.setTitle("title");
//        return project;
//    }
//
//    private ReportModel getReportModel(Long id) {
//        return new ReportModel(id, TOKEN, "rmTitle", "rmDesc", PROJECT_SUP_ID);
//    }
//
//    private Set<Report> generateReportsSetOf(Report... reports) {
//        return Stream.of(reports).collect(Collectors.toSet());
//    }

}