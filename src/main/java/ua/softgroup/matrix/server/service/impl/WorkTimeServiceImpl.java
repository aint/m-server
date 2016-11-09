package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.WorkTimeRepository;
import ua.softgroup.matrix.server.service.WorkTimeService;

import java.util.Optional;

@Service
public class WorkTimeServiceImpl extends AbstractEntityTransactionalService<WorkTime> implements WorkTimeService {

    @Autowired
    public WorkTimeServiceImpl(WorkTimeRepository repository) {
        super(repository);
    }

    @Override
    public Optional<WorkTime> getWorkTimeOfUserAndProject(User user, Project project) {
        return Optional.ofNullable(getRepository().findByUserAndProject(user, project));
    }

    @Override
    protected WorkTimeRepository getRepository() {
        return (WorkTimeRepository) repository;
    }
}
