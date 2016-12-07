package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vadim on 24.10.2016.
 */
public class CurrencyModel implements RetrofitModel {

    @JsonProperty("id") private int id;
    @JsonProperty("name") private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return "CurrencyModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
