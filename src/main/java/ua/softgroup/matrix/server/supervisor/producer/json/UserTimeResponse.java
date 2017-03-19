package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class UserTimeResponse extends TimeResponse {

    private long userId;

    public UserTimeResponse() {
    }

    public UserTimeResponse(long userId, int workSeconds, int idleSeconds, double idlePercentage) {
        this.userId = userId;
        this.workSeconds = workSeconds;
        this.idleSeconds = idleSeconds;
        this.idlePercentage = idlePercentage;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
