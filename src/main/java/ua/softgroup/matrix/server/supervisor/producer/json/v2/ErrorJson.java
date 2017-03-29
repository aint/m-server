package ua.softgroup.matrix.server.supervisor.producer.json.v2;

import com.fasterxml.jackson.annotation.JsonView;
import ua.softgroup.matrix.server.supervisor.producer.json.JsonViewType;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ErrorJson {

    @JsonView(JsonViewType.OUT.class)
    private String message;

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
