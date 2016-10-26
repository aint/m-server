package ua.softgroup.matrix.server.service.impl;

import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.WorkTimeRepository;
import ua.softgroup.matrix.server.service.WorkTimeService;
import ua.softgroup.matrix.server.service.impl.AbstractEntityTransactionalService;

public class WorkTimeServiceImpl extends AbstractEntityTransactionalService<WorkTime> implements WorkTimeService {

    public WorkTimeServiceImpl() {
        repository = applicationContext.getBean(WorkTimeRepository.class);
    }

    @Override
    public WorkTime getWorkTimeOfUserAndProject(User user, Project project) {
        return getRepository().findByUserAndProject(user, project);
    }

    @Override
    protected WorkTimeRepository getRepository() {
        return (WorkTimeRepository) repository;
    }
}
