package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class UserProjectTimeResponse extends TimeResponse {

    private long entityId;
    private String entityType = "project";

    public UserProjectTimeResponse() {
    }

    public UserProjectTimeResponse(long entityId, int workSeconds, int idleSeconds, double idlePercentage) {
        this.entityId = entityId;
        this.totalWorkTimeSeconds = workSeconds;
        this.totalIdleTimeSeconds = idleSeconds;
        this.totalIdlePercentage = idlePercentage;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
