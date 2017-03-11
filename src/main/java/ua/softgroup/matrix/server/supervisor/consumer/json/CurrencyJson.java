package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyJson {

    @JsonProperty
    private Integer id;
    @JsonProperty
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CurrencyJson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
