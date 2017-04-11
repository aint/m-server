package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class UserTimeAndCountResponse extends TimeResponse {

    private long userId;
    private int totalAllSymbolsCount;
    private int windowsSwitchedCount;

    public UserTimeAndCountResponse() {
    }

    public UserTimeAndCountResponse(long userId, int workSeconds, int idleSeconds, double idlePercentage,
                                    int totalAllSymbolsCount, int windowsSwitchedCount) {
        this.userId = userId;
        this.totalWorkTimeSeconds = workSeconds;
        this.totalIdleTimeSeconds = idleSeconds;
        this.totalIdlePercentage = idlePercentage;
        this.totalAllSymbolsCount = totalAllSymbolsCount;
        this.windowsSwitchedCount = windowsSwitchedCount;
    }



    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getTotalAllSymbolsCount() {
        return totalAllSymbolsCount;
    }

    public void setTotalAllSymbolsCount(int totalAllSymbolsCount) {
        this.totalAllSymbolsCount = totalAllSymbolsCount;
    }

    public int getWindowsSwitchedCount() {
        return windowsSwitchedCount;
    }

    public void setWindowsSwitchedCount(int windowsSwitchedCount) {
        this.windowsSwitchedCount = windowsSwitchedCount;
    }
}
