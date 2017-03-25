package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;
import ua.softgroup.matrix.server.supervisor.consumer.json.SettingJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.SettingsJson;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Service
@PropertySource("classpath:desktop.properties")
public class ClientSettingsServiceImpl implements ClientSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(ClientSettingsServiceImpl.class);

    private static final String CHECKPOINT_FREQUENTLY_SECONDS = "checkpoint.frequently.seconds";
    private static final String SCREENSHOT_FREQUENTLY_PERIOD = "screenshot.frequently.period";
    private static final String IDLE_START_SECONDS = "idle.start.seconds";
    private static final String REPORT_EDITABLE_DAYS = "report.editable.days";

    private static final Map<String, Integer> defaultSettings = new HashMap<>();

    private final SupervisorEndpoint supervisorEndpoint;
    private final Environment env;

    @Autowired
    public ClientSettingsServiceImpl(SupervisorEndpoint supervisorEndpoint, Environment env) {
        this.supervisorEndpoint = supervisorEndpoint;
        this.env = env;
    }

    @PostConstruct
    private void initTrackerSettings() {
        defaultSettings.put(CHECKPOINT_FREQUENTLY_SECONDS, Integer.parseInt(env.getRequiredProperty(CHECKPOINT_FREQUENTLY_SECONDS)));
        defaultSettings.put(SCREENSHOT_FREQUENTLY_PERIOD, Integer.parseInt(env.getRequiredProperty(SCREENSHOT_FREQUENTLY_PERIOD)));
        defaultSettings.put(IDLE_START_SECONDS, Integer.parseInt(env.getRequiredProperty(IDLE_START_SECONDS)));
        defaultSettings.put(REPORT_EDITABLE_DAYS, Integer.parseInt(env.getRequiredProperty(REPORT_EDITABLE_DAYS)));
    }

    @Override
    public TrackerSettings getTrackerSettings(String token) {
        try {
            Map<String, Integer> settingsMap = querySettings(token);
            return new TrackerSettings(
                    settingsMap.get(CHECKPOINT_FREQUENTLY_SECONDS),
                    settingsMap.get(SCREENSHOT_FREQUENTLY_PERIOD),
                    settingsMap.get(IDLE_START_SECONDS));
        } catch (IOException e) {
            logger.error("Error when querying settings from supervisor API", e);
            return new TrackerSettings(
                    defaultSettings.get(CHECKPOINT_FREQUENTLY_SECONDS),
                    defaultSettings.get(SCREENSHOT_FREQUENTLY_PERIOD),
                    defaultSettings.get(IDLE_START_SECONDS));
        }
    }

    @Override
    public int getReportEditableDays() {
        return defaultSettings.get(REPORT_EDITABLE_DAYS);
    }

    private Map<String, Integer> querySettings(String token) throws IOException {
        Response<SettingsJson> response = supervisorEndpoint
                .getTrackerSettings(token)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody().string());
        }

        return response.body().getList()
                .stream()
                .collect(Collectors.toMap(SettingJson::getKey, SettingJson::getValue));
    }

}
