package ua.softgroup.matrix.server.security;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.softgroup.matrix.server.api.Constants;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.persistent.SpringDataConfig;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import static java.time.ZoneId.systemDefault;

public class TokenAuthService {

    private String login;
    private String password;

    private StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    private ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringDataConfig.class);
    private UserRepository userRepository = applicationContext.getBean(UserRepository.class);

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
        User user = userRepository.findByUsername(login);
        if (user == null || !user.getUsername().equals(login)) {
            return Constants.INVALID_USERNAME.name();
        } else if (!passwordEncryptor.checkPassword(user.getPassword(), password)) {
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

    public String extractUsername(TokenModel tokenModel) {
        String token = tokenModel.getToken();
        if (Constants.TOKEN_EXPIRED == validateToken(token)) {
            throw new RuntimeException("TOKEN EXPIRED");
        }
        String decryptToken = decryptToken(token);
        System.out.println("decryptToken " + decryptToken);
        String username = decryptToken.substring(0, decryptToken.length() - 13 - 1);
        System.out.println("extractUsername " + username);
        return username;
    }

}
