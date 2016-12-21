package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ActiveProjectsJson {

    @JsonProperty
    private boolean success;
    @JsonProperty
    private List<ProjectJson> list;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ProjectJson> getList() {
        return list;
    }

    public void setList(List<ProjectJson> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ActiveProjectsJson{" +
                "success=" + success +
                ", list=" + list +
                '}';
    }
}
