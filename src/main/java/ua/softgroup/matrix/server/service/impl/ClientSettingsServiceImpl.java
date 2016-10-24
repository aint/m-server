package ua.softgroup.matrix.server.service.impl;

import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.persistent.repository.ClientSettingsRepository;
import ua.softgroup.matrix.server.service.ClientSettingsService;

public class ClientSettingsServiceImpl extends AbstractEntityTransactionalService<ClientSettings> implements ClientSettingsService {

    public ClientSettingsServiceImpl() {
        repository = applicationContext.getBean(ClientSettingsRepository.class);
    }

    @Override
    protected ClientSettingsRepository getRepository() {
        return (ClientSettingsRepository) repository;
    }
}
