package ua.softgroup.matrix.server.model;

import java.io.Serializable;

public class WriteKeyboard extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String words;

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "WriteKeyboard{" +
                "words='" + words + '\'' +
                '}';
    }
}
