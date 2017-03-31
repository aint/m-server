package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;

import java.util.Optional;

public interface WorkTimePeriodService extends GeneralEntityService<WorkTimePeriod> {

    Optional<WorkTimePeriod> getLatestPeriodOf(WorkDay workDay);

}
