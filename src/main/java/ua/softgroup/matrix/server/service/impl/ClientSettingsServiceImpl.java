package ua.softgroup.matrix.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.persistent.repository.ClientSettingsRepository;
import ua.softgroup.matrix.server.service.ClientSettingsService;

@Service
public class ClientSettingsServiceImpl extends AbstractEntityTransactionalService<ClientSettings> implements ClientSettingsService {

    @Autowired
    public ClientSettingsServiceImpl(ClientSettingsRepository  repository) {
        super(repository);
    }

    @Override
    protected ClientSettingsRepository getRepository() {
        return (ClientSettingsRepository) repository;
    }
}
