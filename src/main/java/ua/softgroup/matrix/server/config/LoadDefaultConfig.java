package ua.softgroup.matrix.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author  Vladimir Pihol
 */
@Component
public class LoadDefaultConfig {
    private static final Logger LOG = LoggerFactory.getLogger(LoadDefaultConfig.class);

    private ClientSettings clientSettings;

    private void initClientSettings() {
        try(InputStream in = LoadDefaultConfig.class.getClassLoader().getResourceAsStream("desktop.properties")) {
            Properties prop = new Properties();
            prop.load(in);
            clientSettings = new ClientSettings(
                    Integer.parseInt(prop.getProperty("keyboard.frequently.minutes")),
                    Integer.parseInt(prop.getProperty("screenshot.frequently.minutes")),
                    Integer.parseInt(prop.getProperty("idle.start.minutes")),
                    Integer.parseInt(prop.getProperty("report.editable.days")));
        } catch (IOException e) {
            LOG.error("Error occurred while loading desktop settings from properties file", e);
        }
    }

    public ClientSettings getClientSettings(){
        initClientSettings();
        return this.clientSettings;
    }

}
