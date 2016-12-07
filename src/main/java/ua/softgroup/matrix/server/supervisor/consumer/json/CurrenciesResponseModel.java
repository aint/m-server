package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Vadim on 24.10.2016.
 */
public class CurrenciesResponseModel implements RetrofitModel {

    @JsonProperty("success") private boolean success;
    @JsonProperty("list") private List<CurrencyModel> currencyModelList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<CurrencyModel> getCurrencyModelList() {
        return currencyModelList;
    }

    public void setCurrencyModelList(List<CurrencyModel> currencyModelList) {
        this.currencyModelList = currencyModelList;
    }

    @Override
    public String toString() {
        return "CurrenciesResponseModel{" +
                "success=" + success +
                ", currencyModelList=" + currencyModelList +
                '}';
    }
}
