package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.service.UserService;

@Service
public class UserServiceImpl extends AbstractEntityTransactionalService<User> implements UserService {

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        super(repository);
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
