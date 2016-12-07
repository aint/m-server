package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.desktop.model.UserPassword;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Optional;

public interface UserService extends GeneralEntityService<User> {

    String authenticate(UserPassword userPassword);

    Optional<User> getByUsername(String username);

    Optional<User> getByTrackerToken(String token);

}
