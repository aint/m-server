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

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class WorkTimePeriodRepositoryTest {

    private static final int WORK_PERIOD_COUNT = 10;

    @Autowired
    private WorkTimePeriodRepository workTimePeriodRepository;
    @Autowired
    private TestEntityManager entityManager;

    private WorkDay workDay;
    private LocalDateTime startTime;

    @Before
    public void setUp() throws Exception {
        workDay = new WorkDay();
        entityManager.persist(workDay);

        startTime = LocalDateTime.now();

        IntStream.rangeClosed(0, WORK_PERIOD_COUNT - 1)
                 .forEach(i -> entityManager.persist(new WorkTimePeriod(startTime.plusMinutes(i),
                                                                        startTime.plusMinutes(i * 2),
                                                                        workDay)));
    }

    @After
    public void tearDown() throws Exception {
        entityManager.clear();
    }

    @Test
    public void findTopByWorkDayOrderStartDesc() throws Exception {
        WorkTimePeriod minWorkPeriod = workTimePeriodRepository.findTopByWorkDayOrderByStartAsc(workDay);
        assertTrue(minWorkPeriod.getStart().equals(startTime));
    }

}