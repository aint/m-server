package ua.softgroup.matrix.server.model;

import java.io.Serializable;

public class ProjectModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String projectName;

    private String projectDiscription;

    private double projectPrice;

    public ProjectModel(long id, String projectName, String projectDiscription, double projectPrice) {
        this.id = id;
        this.projectName = projectName;
        this.projectDiscription = projectDiscription;
        this.projectPrice = projectPrice;
    }

    public ProjectModel(String projectName, String projectDiscription, double projectPrice) {
        this.projectName = projectName;
        this.projectDiscription = projectDiscription;
        this.projectPrice = projectPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDiscription() {
        return projectDiscription;
    }

    public void setProjectDiscription(String projectDiscription) {
        this.projectDiscription = projectDiscription;
    }

    public double getProjectPrice() {
        return projectPrice;
    }

    public void setProjectPrice(double projectPrice) {
        this.projectPrice = projectPrice;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", projectDiscription='" + projectDiscription + '\'' +
                ", projectPrice=" + projectPrice +
                '}';
    }
}
