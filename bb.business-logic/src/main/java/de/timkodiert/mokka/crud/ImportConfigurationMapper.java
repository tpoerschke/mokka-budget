package de.timkodiert.mokka.crud;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import de.timkodiert.mokka.domain.ImportConfigurationDTO;
import de.timkodiert.mokka.domain.model.ImportConfiguration;

@Mapper
public interface ImportConfigurationMapper {

    ImportConfigurationDTO importConfigurationToDto(ImportConfiguration importConfiguration);

    void updateImportConfiguration(ImportConfigurationDTO dto, @MappingTarget ImportConfiguration entity);
}
