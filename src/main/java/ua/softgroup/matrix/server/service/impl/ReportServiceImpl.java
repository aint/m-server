package ua.softgroup.matrix.server.service.impl;

import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.ReportRepository;
import ua.softgroup.matrix.server.service.ReportService;

import java.util.Set;

public class ReportServiceImpl extends AbstractEntityTransactionalService<Report> implements ReportService {

    public ReportServiceImpl() {
       repository = applicationContext.getBean(ReportRepository.class);
    }

    @Override
    public Set<Report> getAllReportsOf(User user) {
        return getRepository().findByAuthor(user);
    }

    @Override
    protected ReportRepository getRepository() {
        return (ReportRepository) repository;
    }
}
