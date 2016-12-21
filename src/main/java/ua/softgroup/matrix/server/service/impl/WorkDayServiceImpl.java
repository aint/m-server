package ua.softgroup.matrix.server.service.impl;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.WorkDayService;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Service
public class WorkDayServiceImpl extends AbstractEntityTransactionalService<WorkDay> implements WorkDayService {

    public WorkDayServiceImpl(CrudRepository<WorkDay, Long> repository) {
        super(repository);
    }

    @Override
    public Optional<WorkDay> getByDateAndProject(LocalDate localDate, Project project) {
        return Optional.ofNullable(getRepository().findByDateAndProject(localDate, project));
    }

    @Override
    protected WorkDayRepository getRepository() {
        return (WorkDayRepository) repository;
    }
}
