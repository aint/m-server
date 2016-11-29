package ua.softgroup.matrix.server.supervisor.jersey.json;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ErrorJson {

    @JsonView(JsonViewType.OUT.class)
    private String message;

    public ErrorJson() {
    }

    public ErrorJson(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorJson{" +
                "message='" + message + '\'' +
                '}';
    }
}
