package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.Metrics;
import ua.softgroup.matrix.server.persistent.repository.MetricsRepository;
import ua.softgroup.matrix.server.service.MetricsService;

@Service
public class MetricsServiceImpl extends AbstractEntityTransactionalService<Metrics> implements MetricsService {

    @Autowired
    public MetricsServiceImpl(MetricsRepository repository) {
        super(repository);
    }

    @Override
    protected MetricsRepository getRepository() {
        return (MetricsRepository) repository;
    }
}
