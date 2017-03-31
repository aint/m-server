package ua.softgroup.matrix.server.persistent.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;

import java.time.LocalTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class WorkTimePeriodRepositoryTest {

    private static final int WORK_PERIOD_COUNT = 10;

    @Autowired
    WorkTimePeriodRepository workTimePeriodRepository;
    @Autowired
    TestEntityManager entityManager;

    private WorkDay workDay;
    private LocalTime startTime;

    @Before
    public void setUp() throws Exception {
        workDay = new WorkDay();
        entityManager.persist(workDay);

        startTime = LocalTime.now();

        IntStream.rangeClosed(0, WORK_PERIOD_COUNT - 1)
                 .forEach(i -> entityManager.persist(new WorkTimePeriod(startTime.plusMinutes(i),
                                                                        startTime.plusMinutes(i * 2),
                                                                        workDay))
                 );
    }

    @After
    public void tearDown() throws Exception {
        entityManager.clear();
    }

    @Test
    public void findTopByWorkDayOrderStartDesc() throws Exception {
        WorkTimePeriod minWorkPeriod = workTimePeriodRepository.findTopByWorkDayOrderByStartAsc(workDay);
        assertThat(minWorkPeriod.getStart()).isEqualTo(startTime);
    }

//    @Test
    public void findTopByWorkDayOrderByEndDesc() throws Exception {
        WorkTimePeriod minWorkPeriod = workTimePeriodRepository.findTopByWorkDayOrderByEndDesc(workDay);

        LocalTime dateTime = workDay.getWorkTimePeriods().stream()
                .map(WorkTimePeriod::getEnd)
                .peek(System.out::println)
                .max(LocalTime::compareTo)
                .orElse(null);

        assertThat(minWorkPeriod.getEnd()).isEqualTo(dateTime);
    }

//    @Test
    public void findTopByWorkDayOrderByStartDesc() throws Exception {
        WorkTimePeriod wtp = workTimePeriodRepository.findTopByWorkDayOrderByStartDesc(workDay);
    }

}