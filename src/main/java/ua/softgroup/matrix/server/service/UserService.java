package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.User;

public interface UserService extends GeneralEntityService<User> {

    User getByUsername(String username);

    User getByTrackerToken(String token);

}
