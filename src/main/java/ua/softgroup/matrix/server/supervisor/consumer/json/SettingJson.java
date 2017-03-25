package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingJson {

    @JsonProperty
    private String key;
    @JsonProperty
    private int value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
