package ua.softgroup.matrix.server.persistent.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Report extends AbstractEntity<Long> {
    private static final long serialVersionUID = 8555919835948549711L;

    @Column
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workDay_id")
    private WorkDay workDay;

    @ManyToOne
    private User author;

    @ManyToOne
    private Project project;


    public Report() {
    }

    public Report(Long id) {
        super.setId(id);
    }

    public Report(String title, String description, User author) {
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public Report(String title, String description, Project project, User author) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.author = author;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + super.getId() +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
