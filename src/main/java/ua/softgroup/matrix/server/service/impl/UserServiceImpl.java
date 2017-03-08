package ua.softgroup.matrix.server.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ua.softgroup.matrix.server.desktop.model.datamodels.AuthModel;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;
import ua.softgroup.matrix.server.supervisor.consumer.json.LoginJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.UserJson;

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
    public String authenticate(AuthModel authModel) {
//        if (!validator.validate(up).isEmpty()) return Constants.INVALID_USERNAME.toString();
        try {
            return tryToAuthenticate(authModel.getUsername(), authModel.getPassword());
        } catch (IOException e) {
            LOG.warn("Authentication error: {}", e);
            return authenticateFromDb(authModel.getUsername(), authModel.getPassword());
        }
    }

    private String authenticateFromDb(String username, String password) {
        User user = getRepository().findByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            LOG.info("Offline authentication. Given token {}", user.getTrackerToken());
            return user.getTrackerToken();
        }
        return null;
    }

    private String tryToAuthenticate(String username, String password) throws IOException {
        LoginJson loginJson = executeLoginQuery(username, password).body();
        if (loginJson.getSuccess()) {
            saveUser(loginJson.getUser(), password);
            String token = loginJson.getTrackerToken();
            LOG.info("User authenticated successfully: {}", token);
            return token;
        }
        LOG.info("Authentication failed: {}", loginJson.getMessage());
        return null;
    }

    private Response<LoginJson> executeLoginQuery(String username, String password) throws IOException {
        Response<LoginJson> response = supervisorEndpoint
                .login(username, password)
                .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Oops... Something goes wrong. " + response.errorBody().string());
        }
        return response;
    }

    private void saveUser(UserJson userJson, String password) {
        LOG.debug("UserJson {}", userJson);
        User user = Optional.ofNullable(getRepository().findOne(userJson.getId())).orElse(new User());
        LOG.debug("UserEntity {}", user);
        user.setId(userJson.getId());
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setUsername(userJson.getUsername());
        user.setTrackerToken(userJson.getTrackerToken());
        user.setExternalHourlyRate(userJson.getProfile().getExternalHourlyRate());
        user.setExternalHourlyRateCurrencyId(userJson.getProfile().getExternalHourlyRateCurrencyId());
        user.setInternalHourlyRate(userJson.getProfile().getInternalHourlyRate());
        user.setInternalHourlyRateCurrencyId(userJson.getProfile().getInternalHourlyRateCurrencyId());

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
