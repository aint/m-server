package ua.softgroup.matrix.server.supervisor.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vadim on 24.10.2016.
 */
public class CurrencyModel implements RetrofitModel {

    @SerializedName("id") private int id;
    @SerializedName("name") private String name;

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
