package ua.softgroup.matrix.server.supervisor.jersey;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ErrorJson {

    @JsonView(View.OUT.class)
    private int code;

    @JsonView(View.OUT.class)
    private String message;

    public ErrorJson() {
    }

    public ErrorJson(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
