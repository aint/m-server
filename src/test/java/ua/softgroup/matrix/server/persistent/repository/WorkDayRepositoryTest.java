package ua.softgroup.matrix.server.persistent.repository;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.softgroup.matrix.server.persistent.SpringDataConfig;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

import javax.sql.DataSource;
import java.time.LocalDate;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.ninja_squad.dbsetup.generator.ValueGenerators.dateSequence;
import static com.ninja_squad.dbsetup.generator.ValueGenerators.sequence;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringDataConfig.class })
@ActiveProfiles("test")
@SpringBootTest
public class WorkDayRepositoryTest {

    private static final String WORK_DAY_TABLE = "work_day";
    private static final String WORK_TIME_TABLE = "work_time";
    private static final String PROJECT_TABLE = "project";

    private static final int WORK_DAYS_COUNT = 20;

    private static final Operation DELETE_ALL = sequenceOf(
            deleteAllFrom(WORK_DAY_TABLE, PROJECT_TABLE));

    private static final Operation INSERT_DATA = sequenceOf(
            insertInto(PROJECT_TABLE)
                    .columns("id", "supervisor_id", "title", "description", "author_name", "start_date", "end_date",
                             "rate", "rate_currency_id", "work_started", "idle_started", "today_minutes", "total_minutes",
                             "idle_minutes", "user_id")
                    .values(1L, 1L, "Title", "Description", "Author name", null, null,
                            2L, 1L, null, null, 0L, 0L,
                            0L, null)
                    .build(),
            insertInto(WORK_DAY_TABLE)
                    .withGeneratedValue("id", sequence().startingAt(1L))
                    .withGeneratedValue("work_minutes", sequence().startingAt(0).incrementingBy(100))
                    .withGeneratedValue("idle_minutes", sequence().startingAt(0).incrementingBy(10))
                    .withGeneratedValue("date", dateSequence().startingAt(LocalDate.parse("2016-12-01")).incrementingBy(1, DAYS))
                    .columns("coefficient", "checked", "checker_id", "project_id")
                    .repeatingValues(1.0D, false, null, 1L).times(WORK_DAYS_COUNT)
                    .build());

    @Autowired
    private DataSource dataSource;
    @Autowired
    private WorkDayRepository workDayRepository;

    @Before
    public void setUp() throws Exception {
        new DbSetup(new DataSourceDestination(dataSource), sequenceOf(DELETE_ALL, INSERT_DATA)).launch();
    }
    @After
    public void tearDown() throws Exception {
        new DbSetup(new DataSourceDestination(dataSource), DELETE_ALL).launch();
    }


    @Test
    public void findByDateAndWorkTime() {
        Project project = new Project(1L);
        project.setSupervisorId(1L);
        WorkDay workDay = workDayRepository.findByDateAndProject(LocalDate.parse("2016-12-03"), project);
        assertThat(workDay.getWorkMinutes()).isEqualTo(200);
        assertThat(workDay.getIdleMinutes()).isEqualTo(20);
    }

    @Test
    public void findByDateAndWorkTime_NotFound() {
        Project project = new Project(1L);
        project.setSupervisorId(1L);
        WorkDay workDay = workDayRepository.findByDateAndProject(LocalDate.parse("2015-01-01"), project);
        assertThat(workDay).isNull();
    }

}