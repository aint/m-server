package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CurrenciesJson {

    @JsonProperty
    private boolean success;
    @JsonProperty
    private List<CurrencyJson> list;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<CurrencyJson> getList() {
        return list;
    }

    public void setList(List<CurrencyJson> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "CurrenciesJson{" +
                "success=" + success +
                ", list=" + list +
                '}';
    }
}
