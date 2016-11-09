package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Optional;

public interface UserService extends GeneralEntityService<User> {

    String authenticate(String login, String password);

    User getByUsername(String username);

    Optional<User> getByTrackerToken(String token);

}
