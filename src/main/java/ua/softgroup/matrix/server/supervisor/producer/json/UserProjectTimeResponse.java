package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class UserProjectTimeResponse extends TimeResponse {

    private long projectId;
    private String entityType = "project";

    public UserProjectTimeResponse() {
    }

    public UserProjectTimeResponse(long projectId, int workSeconds, int idleSeconds, double idlePercentage) {
        this.projectId = projectId;
        this.workSeconds = workSeconds;
        this.idleSeconds = idleSeconds;
        this.idlePercentage = idlePercentage;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
