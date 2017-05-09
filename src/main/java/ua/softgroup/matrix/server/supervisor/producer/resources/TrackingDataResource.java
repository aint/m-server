package ua.softgroup.matrix.server.supervisor.producer.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.Screenshot;
import ua.softgroup.matrix.server.persistent.entity.TrackingData;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.UserTimeAndCountResponse;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.GeneralWorkDataJson;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.TrackingDataJson;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.TrackingDataViewType;
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.TrackingPeriodJson;

import javax.validation.constraints.Min;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Base64.getEncoder;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.calculateIdlePercent;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.not;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.parseData;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.validateEndRangeDate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/tracking")
@Api("/tracking")
public class TrackingDataResource {

    private static final Logger logger = LoggerFactory.getLogger(TrackingDataResource.class);

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
    @Path("/project/{entityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "3) getEntityControlData", response = TrackingDataJson.class, responseContainer = "List")
    @Transactional
    @JsonView(TrackingDataViewType.USER.class)
    public Response getTrackingDataByProject(@ApiParam(example = "14") @Min(0) @PathParam("entityId") Long projectId,
                                             @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                             @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        if (projectService.getBySupervisorId(projectId).isEmpty()) {
            throw new NotFoundException();
        }

        LocalDate from = parseData(fromDate);
        LocalDate to = validateEndRangeDate(parseData(toDate));

        List<TrackingDataJson> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(projectId, localDate))
                .filter(not(Set::isEmpty))
                .map(this::convertToProjectTrackingData)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private TrackingDataJson convertToProjectTrackingData(Set<WorkDay> workDays) {
        return new TrackingDataJson(
                getDateOfWorkDay(workDays),
                workDays.stream()
                        .map(workDay -> new GeneralWorkDataJson(
                                workDay.getAuthor().getId(),
                                workDayService.getStartWorkOf(workDay),
                                workDayService.getEndWorkOf(workDay),
                                workDay.getWorkSeconds(),
                                convertWorkTimePeriods(workDay.getWorkTimePeriods())))
                        .collect(Collectors.toList())
        );
    }

    @GET
    @Path("/project/{entityId}/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "4) getEntityUserControlData", response = TrackingDataJson.class, responseContainer = "List")
    @Transactional
    @JsonView(TrackingDataViewType.DATE.class)
    public Response getTrackingDataByProjectAndUser(
                            @ApiParam(example = "14") @Min(0) @PathParam("entityId") Long projectId,
                            @ApiParam(example = "14") @Min(0) @PathParam("userId") Long userId,
                            @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                            @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        if (!userService.getById(userId).isPresent() || projectService.getBySupervisorId(projectId).isEmpty()) {
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
                workDay.getDate(),
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
    @JsonView(TrackingDataViewType.PROJECT.class)
    public Response getTrackingDataByUser(@ApiParam(example = "14") @Min(0) @PathParam("userId") Long userId,
                                          @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                          @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        User user = userService.getById(userId).orElseThrow(NotFoundException::new);

        LocalDate from = parseData(fromDate);
        LocalDate to = validateEndRangeDate(parseData(toDate));

        List<TrackingDataJson> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(user, localDate))
                .filter(not(Set::isEmpty))
                .map(this::convertToUserTrackingData)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    @POST
    @Path("/users/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "8) getFeaturedTogetherControlData", response = UserTimeAndCountResponse.class, responseContainer = "List")
    public Response getFeaturedTogetherControlData(@ApiParam(example = "[1, 2, 13]") @FormParam("usersIds") List<Long> userIds,
                                                   @ApiParam(example = "2017-01-01") @FormParam("fromDate") String fromDate,
                                                   @ApiParam(example = "2017-12-31") @FormParam("toDate") String toDate) {

        LocalDate from = parseData(fromDate);
        LocalDate to = parseData(toDate);

        long userCount = userIds.stream()
                .map(userService::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .count();

        if ((userCount == 0) && (!userIds.isEmpty())) {
            throw new NotFoundException();
        }

        List<UserTimeAndCountResponse> result = userIds.stream()
                .map(userId -> {
                    int workSeconds = workDayService.getTotalWorkSeconds(userId, from, to);
                    int idleSeconds = workDayService.getTotalIdleSeconds(userId, from, to);
                    return new UserTimeAndCountResponse(
                            userId,
                            workSeconds,
                            idleSeconds,
                            calculateIdlePercent(workSeconds, idleSeconds),
                            workDayService.getSymbolsCount(userId, from, to),
                            workDayService.getWindowsSwitchedCount(userId, from, to)
                    );
                })
                .collect(Collectors.toList());


        return Response.ok(result).build();
    }

    private TrackingDataJson convertToUserTrackingData(Set<WorkDay> workDays) {
        return new TrackingDataJson(
                getDateOfWorkDay(workDays),
                workDays.stream()
                        .map(workDay -> new GeneralWorkDataJson(
                                workDay.getProject().getSupervisorId(),
                                "project",
                                workDayService.getStartWorkOf(workDay),
                                workDayService.getEndWorkOf(workDay),
                                workDay.getWorkSeconds(),
                                convertWorkTimePeriods(workDay.getWorkTimePeriods())))
                        .collect(Collectors.toList())
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
                .sorted(Comparator.comparing(WorkTimePeriod::getStart))
                .map(workTimePeriod -> new TrackingPeriodJson(
                        workTimePeriod.getStart(),
                        workTimePeriod.getEnd(),
                        getTrackingData(workTimePeriod).getKeyboardText(),
                        convertRandomScreenshotToBase64(getTrackingData(workTimePeriod).getScreenshots())
                ))
                .collect(Collectors.toList());
    }

    private TrackingData getTrackingData(WorkTimePeriod workTimePeriod) {
        return Optional.ofNullable(workTimePeriod.getTrackingData()).orElseGet(TrackingData::new);
    }

    private String[] convertRandomScreenshotToBase64(Set<Screenshot> screenshots) {
        Screenshot screenshot = screenshots.stream()
                .findAny()
                .orElseGet(Screenshot::new);

        byte[] imageBytes = screenshot.getImageBytes();
        return new String[] {
                "data:image/png;base64," + getEncoder().encodeToString(imageBytes != null ? imageBytes : new byte[]{}),
                screenshot.getScreenshotTitle()};
    }

}