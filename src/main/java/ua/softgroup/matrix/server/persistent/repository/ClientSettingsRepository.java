package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;

public interface ClientSettingsRepository extends CrudRepository<ClientSettings, Long> {

}
