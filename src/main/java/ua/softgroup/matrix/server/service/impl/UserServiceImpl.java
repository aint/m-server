package ua.softgroup.matrix.server.service.impl;

import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.service.UserService;

public class UserServiceImpl extends AbstractEntityTransactionalService<User> implements UserService {

    public UserServiceImpl() {
        repository = applicationContext.getBean(UserRepository.class);
    }

    @Override
    public User getByUsername(String username) {
        return getRepository().findByUsername(username);
    }

    @Override
    public User getByTrackerToken(String token) {
        return getRepository().findByTrackerToken(token);
    }

    @Override
    protected UserRepository getRepository() {
        return (UserRepository) repository;
    }

}
