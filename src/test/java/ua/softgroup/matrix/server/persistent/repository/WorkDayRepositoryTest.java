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
import java.util.Set;
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

    private User user;
    private Project project1;
    private Project project2;
    private LocalDate startDate;

    @Autowired
    WorkDayRepository workDayRepository;
    @Autowired
    TestEntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("1111");
        entityManager.persist(user);
        project1 = new Project();
        project1.setSupervisorId(1L);
        entityManager.persist(project1);
        project2 = new Project();
        project2.setSupervisorId(2L);
        entityManager.persist(project2);

        startDate = LocalDate.now().with(firstDayOfMonth());

        IntStream.rangeClosed(0, WORK_DAYS_COUNT - 1)
                 .forEach(i -> {
                     LocalDate localDate = startDate.plusDays(i);
                     WorkDay workDay = new WorkDay(user, i % 2 == 0 ? project1 : project2, localDate);
                     entityManager.persist(workDay);
                     workDay.setIdleSeconds(i + 1);
                     workDay.setDate(localDate);
                     workDay.setChecked(i % 2 == 0);
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
        randomDays = randomDays % 2 == 0 ? randomDays : randomDays + 1;
        WorkDay workDay = workDayRepository.findByAuthorAndProjectAndDate(user, project1, startDate.plusDays(randomDays));
        assertThat(workDay).isNotNull();
    }

    @Test
    public void findByAuthorAndProjectAndDate_NotFound() throws Exception {
        WorkDay workDay = workDayRepository.findByAuthorAndProjectAndDate(user, project1, startDate.plusDays(WORK_DAYS_COUNT * 2));
        assertThat(workDay).isNull();
    }

    @Test
    public void getCurrentMonthIdleSeconds() throws Exception {
        LocalDate start = LocalDate.now().with(firstDayOfMonth());
        LocalDate end = LocalDate.now().with(lastDayOfMonth());
        Integer monthIdleSeconds = workDayRepository.getCurrentMonthIdleSeconds(user.getId(), project1.getId(), start, end);

        int sum = IntStream.rangeClosed(1, LocalDate.now().lengthOfMonth())
                           .filter(i -> i % 2 != 0)
                           .sum();
        assertThat(monthIdleSeconds).isEqualTo(sum);
    }

    @Test
    public void findByProjectSupervisorIdAndDateBetween() throws Exception {
        LocalDate start = LocalDate.now().with(firstDayOfMonth());
        LocalDate end = LocalDate.now().with(lastDayOfMonth());

        int count = (int) IntStream.rangeClosed(1, LocalDate.now().lengthOfMonth())
                                   .filter(i -> i % 2 == 0)
                                   .count();
        Set<WorkDay> workDays = workDayRepository.findByProjectSupervisorIdAndDateBetween(project2.getSupervisorId(), start, end);
        assertThat(workDays).hasSize(count);
    }

    @Test
    public void findByAuthorIdAndDateBetween() throws Exception {
        final int dayCounts = 10;
        LocalDate start = LocalDate.now().with(firstDayOfMonth());
        LocalDate end = start.plusDays(dayCounts - 1);

        Set<WorkDay> workDays = workDayRepository.findByAuthorIdAndDateBetween(user.getId(), start, end);
        assertThat(workDays).hasSize(dayCounts);
    }

    @Test
    public void findByDateBetween() throws Exception {
        final int dayCounts = 10;
        LocalDate start = LocalDate.now().with(firstDayOfMonth());
        LocalDate end = start.plusDays(dayCounts - 1);

        Set<WorkDay> workDays = workDayRepository.findByDateBetween(start, end);
        assertThat(workDays).hasSize(dayCounts);
    }

    @Test
    public void findByCheckedFalse() throws Exception {
        int count = (int) IntStream.rangeClosed(1, WORK_DAYS_COUNT)
                                   .filter(i -> i % 2 == 0)
                                   .count();

        Set<WorkDay> workDays = workDayRepository.findByCheckedFalse();
        assertThat(workDays).hasSize(count);
    }
}