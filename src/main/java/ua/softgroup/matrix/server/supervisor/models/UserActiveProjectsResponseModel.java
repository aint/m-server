package ua.softgroup.matrix.server.supervisor.models;

import com.google.gson.annotations.SerializedName;
import ua.softgroup.matrix.server.persistent.entity.Project;

import java.util.List;

/**
 * Created by Vadim on 24.10.2016.
 */
public class UserActiveProjectsResponseModel implements RetrofitModel {

    @SerializedName("success") private boolean success;
    @SerializedName("list") private List<Project> projectModelList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Project> getProjectModelList() {
        return projectModelList;
    }

    public void setProjectModelList(List<Project> projectModelList) {
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
