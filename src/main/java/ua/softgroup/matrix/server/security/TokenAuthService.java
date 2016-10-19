package ua.softgroup.matrix.server.security;

import java.time.LocalDateTime;

import static java.time.ZoneId.systemDefault;

public class TokenAuthService {

    private static final String DUMMY_LOGIN = "login";
    private static final String DUMMY_PASSWORD = "password";

    private static String login;
    private static String password;

    private static final int EXPIRATION_PERIOD_DAYS = 30;

    /**
     * Read credential from first line and try to authenticate.
     * @param firstLine credential
     * @return token if authentication success, 'invalid credential' otherwise
     */
    public String authenticate(String firstLine) {
        parseFirstLine(firstLine);
        return tryToAuthenticate();
    }

    private void parseFirstLine(String firstLine) {
        String[] credential = firstLine.split(":");
        login = credential[0];
        password = credential[1];
    }

    private String tryToAuthenticate() {
        if (DUMMY_LOGIN.equals(login) && DUMMY_PASSWORD.equals(password)) {
            return generateToken(login);
        }
        return "invalid credential";
    }

    private String generateToken(String login) {
        return encryptToken(login + ":" + getExpirationDate());
    }

    private long getExpirationDate() {
        return LocalDateTime.now().plusDays(EXPIRATION_PERIOD_DAYS).atZone(systemDefault()).toInstant().toEpochMilli();
    }

    private String encryptToken(String text) {
        //TODO use some encryption algorithm
        return text;
    }

    private String decryptToken(String text) {
        //TODO use some decryption algorithm
        return text;
    }

}
