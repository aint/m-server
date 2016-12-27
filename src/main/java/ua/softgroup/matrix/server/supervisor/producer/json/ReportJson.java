package ua.softgroup.matrix.server.supervisor.producer.json;

import com.fasterxml.jackson.annotation.JsonView;

import java.time.LocalDateTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ReportJson {

    @JsonView(JsonViewType.OUT.class)
    private Long id;

    @JsonView(JsonViewType.OUT.class)
    private LocalDateTime creationDate;

    @JsonView(JsonViewType.OUT.class)
    private LocalDateTime updateDate;

    @JsonView({ JsonViewType.OUT.class, JsonViewType.IN.class })
    private String title;

    @JsonView({ JsonViewType.OUT.class, JsonViewType.IN.class })
    private String description;

    @JsonView(JsonViewType.OUT.class)
    private Long workMinutes;

    @JsonView(JsonViewType.OUT.class)
    private boolean checked;

    public ReportJson(Long id, LocalDateTime creationDate, LocalDateTime updateDate, String title, String description, Long workMinutes, boolean checked) {
        this.id = id;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.title = title;
        this.description = description;
        this.workMinutes = workMinutes;
        this.checked = checked;
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

    public Long getWorkMinutes() {
        return workMinutes;
    }

    public void setWorkMinutes(Long workMinutes) {
        this.workMinutes = workMinutes;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "ReportJson{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", workMinutes=" + workMinutes +
                ", checked=" + checked +
                '}';
    }
}
