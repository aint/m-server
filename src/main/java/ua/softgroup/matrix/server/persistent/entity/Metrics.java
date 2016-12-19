package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Metrics extends AbstractEntity<Long> {
    private static final long serialVersionUID = -1760850406067102667L;

    @ManyToOne
    private WorkTime workTime;

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }
}
