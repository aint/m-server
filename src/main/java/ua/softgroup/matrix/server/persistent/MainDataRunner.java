package ua.softgroup.matrix.server.persistent;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;

public class MainDataRunner {

//    @Autowired
    ApplicationContext applicationContext;

    private UserRepository userRepository;
    private ReportRepository reportRepository;

    public static void main(String[] args) {
        MainDataRunner runner = new MainDataRunner();
        runner.applicationContext = new AnnotationConfigApplicationContext(SpringDataConfig.class);
        runner.userRepository = runner.applicationContext.getBean(UserRepository.class);
        runner.reportRepository = runner.applicationContext.getBean(ReportRepository.class);

        System.out.println("MainDataRunner");
        User user1 = runner.userRepository.findOne(1L);
        System.out.println(user1);

        User user3 = new User("kolia", "IIII AAAA", "1111111");

        Report report1 = new Report("report 1", "desc 1", user1);
        Report report2 = new Report("report 2", "desc 2", user1);
        Report report3 = new Report("report 3", "desc 3", user3);

        runner.userRepository.save(user3);
        runner.reportRepository.save(report1);
        runner.reportRepository.save(report2);
        runner.reportRepository.save(report3);
    }
}
