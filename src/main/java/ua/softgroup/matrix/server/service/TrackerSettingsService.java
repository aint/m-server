package ua.softgroup.matrix.server.service;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public interface TrackerSettingsService {

    TrackerSettings getTrackerSettings(String token);

    class TrackerSettings {
        private int checkpointFrequencyInSeconds;
        private int screenshotPeriodFrequency;
        private int startIdleAfterSeconds;

        public TrackerSettings(int checkpointFrequencyInSeconds, int screenshotPeriodFrequency, int startIdleAfterSeconds) {
            this.checkpointFrequencyInSeconds = checkpointFrequencyInSeconds;
            this.screenshotPeriodFrequency = screenshotPeriodFrequency;
            this.startIdleAfterSeconds = startIdleAfterSeconds;
        }

        public int getCheckpointFrequencyInSeconds() {
            return checkpointFrequencyInSeconds;
        }

        public void setCheckpointFrequencyInSeconds(int checkpointFrequencyInSeconds) {
            this.checkpointFrequencyInSeconds = checkpointFrequencyInSeconds;
        }

        public int getScreenshotPeriodFrequency() {
            return screenshotPeriodFrequency;
        }

        public void setScreenshotPeriodFrequency(int screenshotPeriodFrequency) {
            this.screenshotPeriodFrequency = screenshotPeriodFrequency;
        }

        public int getStartIdleAfterSeconds() {
            return startIdleAfterSeconds;
        }

        public void setStartIdleAfterSeconds(int startIdleAfterSeconds) {
            this.startIdleAfterSeconds = startIdleAfterSeconds;
        }
    }

}
