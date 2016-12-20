package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Vadim on 24.10.2016.
 */
public class UserActiveProjectsResponseModel implements RetrofitModel {

    @JsonProperty("success") private boolean success;
    @JsonProperty("list") private List<ProjectJson> projectModelList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ProjectJson> getProjectModelList() {
        return projectModelList;
    }

    public void setProjectModelList(List<ProjectJson> projectModelList) {
        this.projectModelList = projectModelList;
    }

    @Override
    public String toString() {
        return "UserActiveProjectsResponseModel{" +
                "success=" + success +
                ", projectModelList=" + projectModelList +
                '}';
    }
}
