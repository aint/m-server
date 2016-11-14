package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.AbstractPeriod;
import ua.softgroup.matrix.server.persistent.repository.PeriodRepository;
import ua.softgroup.matrix.server.service.PeriodService;

@Service
public class PeriodServiceImpl extends AbstractEntityTransactionalService<AbstractPeriod> implements PeriodService {

    @Autowired
    public PeriodServiceImpl(PeriodRepository repository) {
        super(repository);
    }

    @Override
    protected PeriodRepository getRepository() {
        return (PeriodRepository) repository;
    }
}
