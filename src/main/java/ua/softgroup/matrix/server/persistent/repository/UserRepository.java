package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Returns a {@code User} by the given {@code username}.
     *
     * @param username userName of the requested user
     * @return a {@code User} or {@code null} if a {@code User} with the given {@code username} not found
     */
    User findByUsername(String username);

}
