package ua.softgroup.matrix.server.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class UserPassword implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    @NotNull
    @Size(min = 2, max = 100)
    private String username;

    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    public UserPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPassword{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
