package de.timkodiert.mokka.domain;

import java.util.List;

public interface ImportConfigurationCrudService {

    List<ImportConfigurationDTO> readAll();

    ImportConfigurationDTO readById(int id);

    boolean create(ImportConfigurationDTO importConfigurationDTO);

    boolean update(ImportConfigurationDTO importConfigurationDTO);

    boolean delete(int id);
}
