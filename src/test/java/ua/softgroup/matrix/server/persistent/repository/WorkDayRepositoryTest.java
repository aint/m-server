package ua.softgroup.matrix.server.persistent.repository;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.softgroup.matrix.server.persistent.SpringDataConfig;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;

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
@TestPropertySource("classpath:test-db.properties")
@ContextConfiguration(classes = { SpringDataConfig.class })
public class WorkDayRepositoryTest {

    private static final String WORK_DAY_TABLE = WorkDay.class.getSimpleName();
    private static final String WORK_TIME_TABLE = WorkTime.class.getSimpleName();

    private static final int WORK_DAYS_COUNT = 20;

    private static final Operation DELETE_ALL = sequenceOf(
            deleteAllFrom(WORK_DAY_TABLE, WORK_TIME_TABLE));

    private static final Operation INSERT_DATA = sequenceOf(
            insertInto(WORK_TIME_TABLE)
                    .columns("id", "startedWork", "todayMinutes", "totalMinutes", "startDowntime", "downtimeMinutes",
                             "rate", "rateCurrencyId", "project_id",  "user_id")
                    .values(1L, null, 0L, 0L, null, 0L, 2L, 1L, null, null)
                    .build(),
            insertInto(WORK_DAY_TABLE)
                    .withGeneratedValue("id", sequence().startingAt(1L))
                    .withGeneratedValue("workMinutes", sequence().startingAt(0).incrementingBy(100))
                    .withGeneratedValue("idleMinutes", sequence().startingAt(0).incrementingBy(10))
                    .withGeneratedValue("date", dateSequence().startingAt(LocalDate.parse("2016-12-01")).incrementingBy(1, DAYS))
                    .columns("coefficient", "checked", "checker_id", "workTime_id")
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
        WorkDay workDay = workDayRepository.findByDateAndWorkTime(LocalDate.parse("2016-12-03"), new WorkTime(1L));
        assertThat(workDay.getWorkMinutes()).isEqualTo(200);
        assertThat(workDay.getIdleMinutes()).isEqualTo(20);
    }

    @Test
    public void findByDateAndWorkTime_NotFound() {
        WorkDay workDay = workDayRepository.findByDateAndWorkTime(LocalDate.parse("2015-01-01"), new WorkTime(1L));
        assertThat(workDay).isNull();
    }

}