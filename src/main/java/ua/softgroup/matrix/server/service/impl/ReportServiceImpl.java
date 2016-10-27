package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.service.ReportService;

import java.util.Set;

@Service
public class ReportServiceImpl extends AbstractEntityTransactionalService<Report> implements ReportService {

    @Autowired
    public ReportServiceImpl(ReportRepository repository) {
        super(repository);
    }

    @Override
    public Set<Report> getAllReportsOf(User user) {
        return getRepository().findByAuthor(user);
    }

    @Override
    public Set<Report> getAllReportsOf(User user, Project project) {
        return getRepository().findByAuthorAndProject(user, project);
    }

    @Override
    protected ReportRepository getRepository() {
        return (ReportRepository) repository;
    }
}
