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

    private static final int DEFAULT_CLIENT_SETTINGS_VERSION = 1;
    private final ClassLoader classLoader = LoadDefaultConfig.class.getClassLoader();

    private ClientSettings clientSettings;
    private int serverPort;
    private Properties prop;
    private String baseUrl;

    public LoadDefaultConfig(){
        this.prop = new Properties();
        readServerPortFromConfig();
    }

    private void initClientSettings() {
        try(InputStream in = classLoader.getResourceAsStream("clientSettings.properties")) {
            prop.load(in);
            clientSettings = new ClientSettings(DEFAULT_CLIENT_SETTINGS_VERSION,
                    Integer.parseInt(prop.getProperty("screenshotUpdateFrequentlyInMinutes")),
                    Integer.parseInt(prop.getProperty("keyboardUpdateFrequentlyInMinutes")),
                    Integer.parseInt(prop.getProperty("startDowntimeAfterInMinutes")),
                    Integer.parseInt(prop.getProperty("reportEditablePeriodInDays"))
            );
        } catch (IOException e) {
            LOG.error("Error occurred while loading server settings from properties file", e);
        }
    }

    private void readServerPortFromConfig(){
        try(InputStream in = classLoader.getResourceAsStream("server.properties")) {
            prop.load(in);
            this.serverPort = Integer.valueOf(prop.getProperty("server.port"));
            this.baseUrl = prop.getProperty("supervisor.api");
        } catch (Exception ex){
            LOG.error("Error occurred while loading server settings from properties file", ex);
        }
    }

    public int getServerPort(){
        return this.serverPort;
    }

    public ClientSettings getClientSettings(){
        initClientSettings();
        return this.clientSettings;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
