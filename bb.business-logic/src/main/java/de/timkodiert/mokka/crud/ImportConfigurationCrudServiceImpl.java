package de.timkodiert.mokka.crud;

import java.util.List;

import jakarta.inject.Inject;
import org.mapstruct.factory.Mappers;

import de.timkodiert.mokka.domain.ImportConfigurationCrudService;
import de.timkodiert.mokka.domain.ImportConfigurationDTO;
import de.timkodiert.mokka.domain.model.ImportConfiguration;
import de.timkodiert.mokka.domain.repository.ImportConfigurationsRepository;

public class ImportConfigurationCrudServiceImpl implements ImportConfigurationCrudService {

    private final ImportConfigurationsRepository importConfigurationsRepository;

    @Inject
    public ImportConfigurationCrudServiceImpl(ImportConfigurationsRepository importConfigurationsRepository) {
        this.importConfigurationsRepository = importConfigurationsRepository;
    }

    @Override
    public List<ImportConfigurationDTO> readAll() {
        ImportConfigurationMapper mapper = Mappers.getMapper(ImportConfigurationMapper.class);
        return importConfigurationsRepository.findAll().stream().map(mapper::importConfigurationToDto).toList();
    }

    @Override
    public ImportConfigurationDTO readById(int id) {
        ImportConfigurationMapper mapper = Mappers.getMapper(ImportConfigurationMapper.class);
        return mapper.importConfigurationToDto(importConfigurationsRepository.findById(id));
    }

    @Override
    public boolean create(ImportConfigurationDTO importConfigurationDTO) {
        ImportConfiguration importConfiguration = new ImportConfiguration();
        ImportConfigurationMapper mapper = Mappers.getMapper(ImportConfigurationMapper.class);
        mapper.updateImportConfiguration(importConfigurationDTO, importConfiguration);
        importConfigurationsRepository.persist(importConfiguration);
        return true;
    }

    @Override
    public boolean update(ImportConfigurationDTO importConfigurationDTO) {
        ImportConfiguration importConfiguration = importConfigurationsRepository.findById(importConfigurationDTO.getId());
        ImportConfigurationMapper mapper = Mappers.getMapper(ImportConfigurationMapper.class);
        mapper.updateImportConfiguration(importConfigurationDTO, importConfiguration);
        importConfigurationsRepository.persist(importConfiguration);
        return true;
    }

    @Override
    public boolean delete(int id) {
        ImportConfiguration importConfiguration = importConfigurationsRepository.findById(id);
        if (importConfiguration == null) {
            return false;
        }
        importConfigurationsRepository.remove(importConfiguration);
        return true;
    }
}
