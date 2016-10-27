package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.TimePeriod;
import ua.softgroup.matrix.server.persistent.repository.TimePeriodRepository;
import ua.softgroup.matrix.server.service.TimePeriodService;

@Service
public class TimePeriodServiceImpl extends AbstractEntityTransactionalService<TimePeriod> implements TimePeriodService {

    @Autowired
    public TimePeriodServiceImpl(TimePeriodRepository repository) {
        super(repository);
    }

    @Override
    protected TimePeriodRepository getRepository() {
        return (TimePeriodRepository) repository;
    }
}
