package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Tracking;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.TrackingRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.TrackingService;
import ua.softgroup.matrix.server.service.WorkDayService;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
public class TrackingServiceImpl extends AbstractEntityTransactionalService<Tracking> implements TrackingService {

    private final ProjectService projectService;
    private final WorkDayService workDayService;

    @Autowired
    public TrackingServiceImpl(TrackingRepository repository, ProjectService projectService, WorkDayService workDayService) {
        super(repository);
        this.projectService = projectService;
        this.workDayService = workDayService;
    }

    @Nonnull
    @Override
    public Tracking getByProjectIdAndDate(Long projectId, LocalDate date) {
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
        WorkDay workDay = workDayService.getByDateAndProject(date, project).orElseThrow(NoSuchElementException::new);
        Tracking tracking = getRepository().findByWorkDay(workDay);
        return tracking != null ? tracking : new Tracking(workDay);
    }

    @Override
    protected TrackingRepository getRepository() {
        return (TrackingRepository) repository;
    }
}
