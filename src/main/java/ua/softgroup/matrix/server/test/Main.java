package ua.softgroup.matrix.server.test;

import org.jasypt.util.text.StrongTextEncryptor;
import ua.softgroup.matrix.server.api.Constants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import static java.awt.SystemColor.text;
import static java.time.ZoneId.systemDefault;

public class Main {

    private static final int EXPIRATION_PERIOD_DAYS = 30;

    public static void main(String[] args) {
        String login = "vasia";
        Main main = new Main();
        String token = main.generateToken(login);
        System.out.println(token);
        String decryptToken = main.decryptToken(token);
        System.out.println(decryptToken);
        String timestamp = decryptToken.substring(decryptToken.length() - 13);
        System.out.println(timestamp);
        LocalDate expirationDate = Instant.ofEpochMilli(Long.valueOf(timestamp)).atZone(ZoneId.systemDefault()).toLocalDate();
        if (LocalDate.now().isAfter(expirationDate)) {
            System.out.println("expired");
        } else {
            System.out.println("ok");
        }

    }


    private boolean checkExpirationDate(Long timestamp) {
        LocalDate expirationDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        return !LocalDate.now().isAfter(expirationDate);
    }

    private String encryptToken(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    private String decryptToken(String text) {
        return new String(Base64.getDecoder().decode(text));
    }

    private long getExpirationDate() {
        return LocalDateTime.now().plusDays(EXPIRATION_PERIOD_DAYS).atZone(systemDefault()).toInstant().toEpochMilli();
    }

    private String generateToken(String login) {
        return encryptToken(login + ":" + getExpirationDate());
    }

}
