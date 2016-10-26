package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;

public interface WorkTimeService extends GeneralEntityService<WorkTime> {

    WorkTime getWorkTimeOfUserAndProject(User user, Project project);

}
