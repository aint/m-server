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
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.IntStream;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class WorkDayRepositoryTest {

    private static final int WORK_DAYS_COUNT = 40;

    @Autowired
    private WorkDayRepository workDayRepository;
    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Project project;
    private LocalDate startDate;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("1111");
        entityManager.persist(user);
        project = new Project();
        entityManager.persist(project);

        startDate = LocalDate.now().with(firstDayOfMonth());

        IntStream.rangeClosed(0, WORK_DAYS_COUNT - 1)
                 .forEach(i -> {
                     LocalDate localDate = startDate.plusDays(i);
                     WorkDay workDay = new WorkDay(user, project, localDate);
                     entityManager.persist(workDay);
                     workDay.setIdleSeconds(i + 1);
                     workDay.setDate(localDate);
                     entityManager.persist(workDay);
                 });
    }

    @After
    public void tearDown() throws Exception {
        entityManager.clear();
    }


    @Test
    public void findByAuthorAndProjectAndDate() throws Exception {
        int randomDays = new Random().nextInt(WORK_DAYS_COUNT);
        WorkDay workDay = workDayRepository.findByAuthorAndProjectAndDate(user, project, startDate.plusDays(randomDays));
        assertThat(workDay).isNotNull();
    }

    @Test
    public void findByAuthorAndProjectAndDate_NotFound() throws Exception {
        WorkDay workDay = workDayRepository.findByAuthorAndProjectAndDate(user, project, startDate.plusDays(WORK_DAYS_COUNT * 2));
        assertThat(workDay).isNull();
    }

    @Test
    public void getCurrentMonthIdleSeconds() throws Exception {
        LocalDate start = LocalDate.now().with(firstDayOfMonth());
        LocalDate end = LocalDate.now().with(lastDayOfMonth());
        Integer monthIdleSeconds = workDayRepository.getCurrentMonthIdleSeconds(user.getId(), project.getId(), start, end);
        int sum = IntStream.rangeClosed(1, LocalDate.now().lengthOfMonth()).sum();
        assertThat(monthIdleSeconds).isEqualTo(sum);
    }

}