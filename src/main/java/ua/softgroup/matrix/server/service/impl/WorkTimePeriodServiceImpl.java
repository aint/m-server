package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.persistent.repository.WorkTimePeriodRepository;
import ua.softgroup.matrix.server.service.WorkTimePeriodService;

import java.util.Optional;

@Service
public class WorkTimePeriodServiceImpl extends AbstractEntityTransactionalService<WorkTimePeriod> implements WorkTimePeriodService {

    @Autowired
    public WorkTimePeriodServiceImpl(WorkTimePeriodRepository repository) {
        super(repository);
    }

    @Override
    public Optional<WorkTimePeriod> getLatestPeriodOf(WorkDay workDay) {
        return Optional.ofNullable(getRepository().findTopByWorkDayOrderByStartDesc(workDay));
    }

    @Override
    protected WorkTimePeriodRepository getRepository() {
        return (WorkTimePeriodRepository) repository;
    }
}
