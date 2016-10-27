package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.Downtime;
import ua.softgroup.matrix.server.persistent.repository.DowntimeRepository;
import ua.softgroup.matrix.server.service.DowntimeService;

@Service
public class DowntimeServiceImpl extends AbstractEntityTransactionalService<Downtime> implements DowntimeService {

    @Autowired
    public DowntimeServiceImpl(DowntimeRepository repository) {
        super(repository);
    }

    @Override
    protected DowntimeRepository getRepository() {
        return (DowntimeRepository) repository;
    }
}
