package ua.softgroup.matrix.server.service.impl;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;

import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringRunner.class)
public class WorkDayServiceImplTest {

    private static final String TOKEN = "token_value";
    private static final Long PROJECT_ID = 42L;

    @MockBean
    private WorkDayRepository repository;
    @MockBean
    private UserService userService;
    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private Validator validator;
    @MockBean
    private Environment environment;

    private WorkDayService workDayService;

    private User user = createUser();
    private Project project = createProject();

    @Before
    public void setUp() throws Exception {
        when(userService.getByTrackerToken(TOKEN)).thenReturn(Optional.of(user));
        when(projectRepository.findOne(PROJECT_ID)).thenReturn(project);

        workDayService = new WorkDayServiceImpl(repository, projectRepository, userService, validator, environment);
    }

//    @Test
    public void getByDateAndProject() throws Exception {

    }

//    @Test
    public void getAllWorkDaysOf() throws Exception {

    }

//    @Test
    public void getWorkDaysOf() throws Exception {

    }

//    @Test
    public void saveReportOrUpdate() throws Exception {

    }

//    @Test
    public void convertEntityToJson() throws Exception {

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

    private WorkDay createWorkDay(Long id, LocalDate date) {
        WorkDay workDay = new WorkDay(id);
        workDay.setDate(date);
        return workDay;
    }

}