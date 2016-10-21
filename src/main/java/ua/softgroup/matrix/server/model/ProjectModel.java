package ua.softgroup.matrix.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Projects")
public class ProjectModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Project_Name")
    private String projectName;

    @Column(name = "Project_Description")
    private String projectDiscription;

    @Column(name = "Project_Price")
    private double projectPrice;

    public ProjectModel(String projectName, String projectDiscription, double projectPrice) {
        this.projectName = projectName;
        this.projectDiscription = projectDiscription;
        this.projectPrice = projectPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
