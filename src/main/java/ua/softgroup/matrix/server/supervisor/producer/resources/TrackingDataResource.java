package ua.softgroup.matrix.server.supervisor.producer.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.Screenshot;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.GeneralWorkDataJson;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.TrackingDataJson;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.TrackingDataViewType;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.TrackingPeriodJson;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.softgroup.matrix.server.supervisor.producer.Utils.not;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.parseData;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/tracking")
@Api("/tracking")
public class TrackingDataResource {

    private final UserService userService;
    private final ProjectService projectService;
    private final WorkDayService workDayService;

    public TrackingDataResource(UserService userService,
                                ProjectService projectService,
                                WorkDayService workDayService) {
        this.userService = userService;
        this.projectService = projectService;
        this.workDayService = workDayService;
    }


    @GET
    @Path("/{entityType}/{entityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "3) getEntityControlData", response = TrackingDataJson.class, responseContainer = "List")
    @Transactional
    @JsonView(TrackingDataViewType.PROJECT.class)
    public Response getTrackingDataByProject(@ApiParam(example = "14") @Min(0) @PathParam("entityId") Long projectId,
                                             @ApiParam(example = "projects") @PathParam("entityType") String entityType,
                                             @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                             @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        if (projectService.getBySupervisorId(projectId).isEmpty()) {
            throw new NotFoundException();
        }

        LocalDate from = parseData(fromDate);
        LocalDate to = parseData(toDate);
        to = to.isAfter(LocalDate.now()) ? LocalDate.now().plusDays(1) : to;

        List<TrackingDataJson> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(projectId, localDate))
                .filter(not(Set::isEmpty))
                .map(this::convertToProjectTrackingData)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private TrackingDataJson convertToProjectTrackingData(Set<WorkDay> workDays) {
        List<TrackingPeriodJson> trackingDataPeriods = workDays.stream()
                .flatMap(workDay -> convertWorkTimePeriods(workDay.getWorkTimePeriods()).stream())
                .collect(Collectors.toList());

        return new TrackingDataJson(
                getDateOfWorkDay(workDays),
                workDays.stream()
                        .map(workDay -> new GeneralWorkDataJson(
                                workDay.getAuthor().getId(),
                                workDayService.getStartWorkOf(workDay),
                                workDayService.getEndWorkOf(workDay),
                                workDay.getWorkSeconds(),
                                trackingDataPeriods))
                        .collect(Collectors.toList())
        );
    }

    @GET
    @Path("/{entityType}/{entityId}/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "4) getEntityUserControlData", response = TrackingDataJson.class, responseContainer = "List")
    @Transactional
    public Response getTrackingDataByProjectAndUser(
                            @ApiParam(example = "14") @Min(0) @PathParam("entityId") Long projectId,
                            @ApiParam(example = "projects")   @PathParam("entityType") String entityType,
                            @ApiParam(example = "14") @Min(0) @PathParam("userId") Long userId,
                            @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                            @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        if (projectService.getBySupervisorId(projectId).isEmpty() || !userService.getById(userId).isPresent()) {
            throw new NotFoundException();
        }

        List<GeneralWorkDataJson> result =
                workDayService.getAllWorkDaysOf(userId, projectId, parseData(fromDate), parseData(toDate))
                        .stream()
                        .map(this::convertToUserAndProjectTrackingData)
                        .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private GeneralWorkDataJson convertToUserAndProjectTrackingData(WorkDay workDay) {
        return new GeneralWorkDataJson(
                workDayService.getStartWorkOf(workDay),
                workDayService.getEndWorkOf(workDay),
                workDay.getWorkSeconds(),
                convertWorkTimePeriods(workDay.getWorkTimePeriods())
        );
    }

    @GET
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "5) getUserControlData", response = TrackingDataJson.class, responseContainer = "List")
    @Transactional
    @JsonView(TrackingDataViewType.USER.class)
    public Response getTrackingDataByUser(@ApiParam(example = "14") @Min(0) @PathParam("userId") Long userId,
                                          @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                          @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        userService.getById(userId).orElseThrow(NotFoundException::new);

        List<GeneralWorkDataJson> result =
                workDayService.getUserWorkDaysBetween(userId, parseData(fromDate), parseData(toDate))
                        .stream()
                        .map(this::convertToUserTrackingData)
                        .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private GeneralWorkDataJson convertToUserTrackingData(WorkDay workDay) {
        return new GeneralWorkDataJson(
                workDay.getProject().getSupervisorId(),
                null,
                workDayService.getStartWorkOf(workDay),
                workDayService.getEndWorkOf(workDay),
                workDay.getWorkSeconds(),
                convertWorkTimePeriods(workDay.getWorkTimePeriods())
        );
    }

    private LocalDate getDateOfWorkDay(Set<WorkDay> workDays) {
        return workDays.stream()
                .map(WorkDay::getDate)
                .findAny()
                .orElse(null);
    }

    private List<TrackingPeriodJson> convertWorkTimePeriods(Set<WorkTimePeriod> workTimePeriods) {
        return workTimePeriods.stream()
                .map(workTimePeriod -> new TrackingPeriodJson(
                        workTimePeriod.getStart(),
                        workTimePeriod.getEnd(),
                        null,
                        workTimePeriod.getTrackingData().getKeyboardText(),
                        convertRandomScreenshotToBase64(workTimePeriod.getTrackingData().getScreenshots())
                ))
                .collect(Collectors.toList());
    }

    private String convertRandomScreenshotToBase64(Set<Screenshot> screenshots) {
        return screenshots.stream()
                .map(screenshot -> Base64.getEncoder().encodeToString(screenshot.getImageBytes()))
                .findAny()
                .orElse(null);
    }

}