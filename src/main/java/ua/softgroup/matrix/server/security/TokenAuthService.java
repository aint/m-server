package ua.softgroup.matrix.server.security;

import org.jasypt.util.password.StrongPasswordEncryptor;
import ua.softgroup.matrix.server.api.Constants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import static java.time.ZoneId.systemDefault;

public class TokenAuthService {

    private static final String DUMMY_LOGIN = "ivan";
    private static final String DUMMY_PASSWORD = "123456";

    private String login;
    private String password;

    private StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    private static final int EXPIRATION_PERIOD_DAYS = 30;

    /**
     * Read credential from first line and try to authenticate.
     *
     * @param login    login
     * @param password password
     * @return token if authentication success, 'invalid credential' otherwise
     */
    public String authenticate(String login, String password) {
        this.login = login;
        this.password = password;

        return tryToAuthenticate();
    }

    private String tryToAuthenticate() {
        if (!DUMMY_LOGIN.equals(login)) {
            return Constants.INVALID_USERNAME.name();
        } else if (!passwordEncryptor.checkPassword(DUMMY_PASSWORD, password)) {
            return Constants.INVALID_PASSWORD.name();
        }
        return generateToken(login);
    }

    private String generateToken(String login) {
        return encryptToken(login + ":" + getExpirationDate());
    }

    private long getExpirationDate() {
        return LocalDateTime.now().plusDays(EXPIRATION_PERIOD_DAYS).atZone(systemDefault()).toInstant().toEpochMilli();
    }

    private String encryptToken(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    private String decryptToken(String text) {
        return new String(Base64.getDecoder().decode(text));
    }

    public Constants validateToken(String encryptedToken) {
        String token = decryptToken(encryptedToken);
        System.out.println("decryptToken " + token);
        String timestamp = token.substring(token.length() - 13);
        return isDateExpired(Long.valueOf(timestamp)) ? Constants.TOKEN_EXPIRED : Constants.TOKEN_VALIDATED;
    }

    private boolean isDateExpired(Long timestamp) {
        LocalDate expirationDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        return LocalDate.now().isAfter(expirationDate);
    }

}
