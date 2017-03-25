package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class SettingsJson {

    @JsonProperty
    private boolean success;
    @JsonProperty
    private List<SettingJson> list;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SettingJson> getList() {
        return list;
    }

    public void setList(List<SettingJson> list) {
        this.list = list;
    }
}
