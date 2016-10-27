package ua.softgroup.matrix.server.security;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import ua.softgroup.matrix.server.api.Constants;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.LoginResponseModel;
import ua.softgroup.matrix.server.supervisor.models.UserModel;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import static java.time.ZoneId.systemDefault;

@Component
public class TokenAuthService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthService.class);

    private String login;
    private String password;

    private StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    @Autowired
    private UserRepository userRepository;

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

        try {
            return tryToAuthenticate();
        } catch (IOException e) {
            LOG.error("Auth error", e);
            // TODO return invalid credentials
            return Constants.INVALID_USERNAME.name();
        }
    }

    private Response<LoginResponseModel> executeLoginQuery() throws IOException {
        Call<LoginResponseModel> call = SupervisorQueriesSingleton.getInstance().getSupervisorQueries().login(login, password);
        return call.execute();
    }

    private String tryToAuthenticate() throws IOException {
        Response<LoginResponseModel> response = executeLoginQuery();
        if (response.isSuccessful() && response.body().getSuccess()) {
            LoginResponseModel loginResponse = response.body();
            String token = loginResponse.getTrackerToken();
            saveUser(loginResponse.getUserModel());
            LOG.debug("Auth token {}", token);
            return token;
        } else {
            // TODO return invalid credentials
            return Constants.INVALID_USERNAME.name();
        }
    }

    private void saveUser(UserModel userModel) {
        LOG.debug("UserModel {}", userModel);
        User user = userRepository.findOne(userModel.getId());
        LOG.debug("User {}", user);
        if (user == null) {
            user = new User();
        }
        user.setId(userModel.getId());
        user.setPassword(password);
        user.setEmail(userModel.getEmail());
        user.setUsername(userModel.getUsername());
        user.setTrackerToken(userModel.getTrackerToken());
        user.setFirstName(userModel.getProfileModel().getFirstName());
        user.setLastName(userModel.getProfileModel().getLastName());
        user.setMiddleName(userModel.getProfileModel().getMiddleName());
        user.setMonthlyRate(userModel.getProfileModel().getMonthlyRate());
        user.setMonthlyRateCurrencyId(userModel.getProfileModel().getMonthlyRateCurrencyId());
        user.setExternalHourlyRate(userModel.getProfileModel().getExternalHourlyRate());
        user.setExternalHourlyRateCurrencyId(userModel.getProfileModel().getExternalHourlyRateCurrencyId());
        user.setInternalHourlyRate(userModel.getProfileModel().getInternalHourlyRate());
        user.setInternalHourlyRateCurrencyId(userModel.getProfileModel().getInternalHourlyRateCurrencyId());
        user.setEmailHome(userModel.getProfileModel().getEmailHome());

        userRepository.save(user);
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

//    public Constants validateToken(String encryptedToken) {
//        String token = decryptToken(encryptedToken);
//        String timestamp = token.substring(token.length() - 13);
//        return isDateExpired(Long.valueOf(timestamp)) ? Constants.TOKEN_EXPIRED : Constants.TOKEN_VALIDATED;
//    }

    private boolean isDateExpired(Long timestamp) {
        LocalDate expirationDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        return LocalDate.now().isAfter(expirationDate);
    }

//    public String extractUsername(TokenModel tokenModel) {
//        String token = tokenModel.getToken();
//        if (Constants.TOKEN_EXPIRED == validateToken(token)) {
//            throw new RuntimeException("TOKEN EXPIRED");
//        }
//        String decryptToken = decryptToken(token);
//        LOG.debug("token {}", decryptToken);
//        String username = decryptToken.substring(0, decryptToken.length() - 13 - 1);
//        LOG.debug("username {}", username);
//        return username;
//    }

}
