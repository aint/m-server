package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Set;

public interface ReportService extends GeneralEntityService<Report> {

    Set<Report> getAllReportsOf(User user);

    Set<Report> getAllReportsOf(User user, Project project);

}
