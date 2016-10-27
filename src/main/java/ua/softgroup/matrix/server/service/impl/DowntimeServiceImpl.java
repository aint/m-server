package ua.softgroup.matrix.server.service.impl;

import ua.softgroup.matrix.server.persistent.entity.Downtime;
import ua.softgroup.matrix.server.persistent.repository.DowntimeRepository;
import ua.softgroup.matrix.server.service.DowntimeService;

public class DowntimeServiceImpl extends AbstractEntityTransactionalService<Downtime> implements DowntimeService {

    public DowntimeServiceImpl() {
        repository = applicationContext.getBean(DowntimeRepository.class);
    }

    @Override
    protected DowntimeRepository getRepository() {
        return (DowntimeRepository) repository;
    }
}
