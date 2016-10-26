package ua.softgroup.matrix.server.service.impl;

import ua.softgroup.matrix.server.persistent.entity.TimePeriod;
import ua.softgroup.matrix.server.persistent.repository.TimePeriodRepository;
import ua.softgroup.matrix.server.service.TimePeriodService;

public class TimePeriodServiceImpl extends AbstractEntityTransactionalService<TimePeriod> implements TimePeriodService {

    public TimePeriodServiceImpl() {
        repository = applicationContext.getBean(TimePeriodRepository.class);
    }

    @Override
    protected TimePeriodRepository getRepository() {
        return (TimePeriodRepository) repository;
    }
}
