package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ua.softgroup.matrix.server.desktop.api.Constants;
import ua.softgroup.matrix.server.desktop.model.UserPassword;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;
import ua.softgroup.matrix.server.supervisor.consumer.json.LoginResponseModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.UserModel;

import javax.validation.Validator;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserServiceImpl extends AbstractEntityTransactionalService<User> implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Validator validator;
    private final SupervisorEndpoint supervisorEndpoint;

    @Autowired
    public UserServiceImpl(UserRepository repository, Validator validator, SupervisorEndpoint supervisorEndpoint) {
        super(repository);
        this.validator = validator;
        this.supervisorEndpoint = supervisorEndpoint;
    }

    @Override
    public String authenticate(UserPassword up) {
        if (!validator.validate(up).isEmpty()) return Constants.INVALID_USERNAME.toString();
        try {
            return tryToAuthenticate(up.getUsername(), up.getPassword());
        } catch (IOException e) {
            LOG.warn("Authentication error: {}", e);
            return authenticateFromDb(up.getUsername(), up.getPassword());
        }
    }

    private String authenticateFromDb(String username, String password) {
        User user = getRepository().findByUsername(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                LOG.info("Offline authentication. Given token {}", user.getTrackerToken());
                return user.getTrackerToken();
            }
            return Constants.INVALID_PASSWORD.name();
        }
        return Constants.INVALID_USERNAME.name();
    }

    private String tryToAuthenticate(String username, String password) throws IOException {
        LoginResponseModel loginResponseModel = executeLoginQuery(username, password).body();
        if (loginResponseModel.getSuccess()) {
            saveUser(loginResponseModel.getUserModel(), password);
            String token = loginResponseModel.getTrackerToken();
            LOG.info("Given token {}", token);
            return token;
        }
        String message = loginResponseModel.getMessage();
        LOG.info("Authentication failed: {}", message);
        return message.contains("password")
                ? Constants.INVALID_PASSWORD.name()
                : Constants.INVALID_USERNAME.name();
    }

    private Response<LoginResponseModel> executeLoginQuery(String username, String password) throws IOException {
        Response<LoginResponseModel> response = supervisorEndpoint
                .login(username, password)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }
        return response;
    }

    //TODO user with ID should update automatically
    private void saveUser(UserModel userModel, String password) {
        LOG.debug("UserModel {}", userModel);
        User user = getRepository().findOne(userModel.getId());
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

        getRepository().save(user);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return Optional.ofNullable(getRepository().findByUsername(username));
    }

    @Override
    public Optional<User> getByTrackerToken(String token) {
        return Optional.ofNullable(getRepository().findByTrackerToken(token));
    }

    @Override
    protected UserRepository getRepository() {
        return (UserRepository) repository;
    }

}
