package ua.softgroup.matrix.server.request;

import java.io.Serializable;
import java.time.LocalDate;

public class Report implements Serializable {

    private static final long serialVersionUID = -501768090960673517L;

    private String text;
    private LocalDate date;

    public Report(String text, LocalDate date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
