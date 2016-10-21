package ua.softgroup.matrix.server.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.SpringDataConfig;
import ua.softgroup.matrix.server.persistent.repository.UserRepository;
import ua.softgroup.matrix.server.service.GeneralEntityService;

import java.util.*;

public abstract class AbstractEntityTransactionalService<T> implements GeneralEntityService<T> {

    protected ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringDataConfig.class);

    protected CrudRepository<T, Long> repository;

    protected abstract CrudRepository<T, Long> getRepository();

    @Override
    public T getById(Long id) {
        return repository.findOne(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public Collection<T> getAll() {
        Set<T> entities = new HashSet<>();
        repository.findAll().forEach(entities::add);
        return entities;
    }

    @Override
    public boolean isExist(Long id) {
        return repository.exists(id);
    }


}
