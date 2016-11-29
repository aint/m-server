package ua.softgroup.matrix.server.persistent.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ua.softgroup.matrix.server.supervisor.jersey.json.JsonViewType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(JsonViewType.OUT.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(JsonViewType.OUT.class)
    @Column
    @CreationTimestamp
    private LocalDateTime creationDate;

    @JsonView(JsonViewType.OUT.class)
    @Column
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @JsonView({ JsonViewType.OUT.class, JsonViewType.IN.class })
    @Column
    private String title;

    @JsonView({ JsonViewType.OUT.class, JsonViewType.IN.class })
    @Column(columnDefinition = "TEXT")
    private String description;

    @JsonView({ JsonViewType.OUT.class, JsonViewType.IN.class })
    @Column(nullable = false)
    private boolean checked = false;

    @JsonIgnore
    @ManyToOne
    private User checker;

    @JsonIgnore
    @ManyToOne
    private User author;

    @JsonIgnore
    @ManyToOne
    private Project project;


    public Report() {
    }

    public Report(Long id) {
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public User getChecker() {
        return checker;
    }

    public void setChecker(User checker) {
        this.checker = checker;
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
                "id=" + id +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                '}';
    }
}
