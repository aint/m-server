package ua.softgroup.matrix.server.service.impl;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.service.GeneralEntityService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractEntityTransactionalService<T> implements GeneralEntityService<T> {

    protected CrudRepository<T, Long> repository;

    protected AbstractEntityTransactionalService(CrudRepository<T, Long> repository) {
        this.repository = repository;
    }

    protected abstract CrudRepository<T, Long> getRepository();

    @Override
    public Optional<T> getById(Long id) {
        return Optional.ofNullable(repository.findOne(id));
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
