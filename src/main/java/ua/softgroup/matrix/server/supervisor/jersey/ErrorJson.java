package ua.softgroup.matrix.server.supervisor.jersey;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ErrorJson {

    private int code;

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
