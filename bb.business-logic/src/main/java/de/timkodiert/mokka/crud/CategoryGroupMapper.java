package de.timkodiert.mokka.crud;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.CategoryGroup;

@Mapper
public interface CategoryGroupMapper {

    @Mapping(target = "categories", source = "categoryGroup")
    CategoryGroupDTO categoryGroupToDto(CategoryGroup categoryGroup);

    default List<Reference<CategoryDTO>> mapCategories(CategoryGroup categoryGroup) {
        return categoryGroup.getCategories()
                            .stream()
                            .map(c -> new Reference<>(CategoryDTO.class, c.getId(), c.getName()))
                            .toList();
    }

    @Mapping(target = "categories", ignore = true)
    void updateCategoryGroup(CategoryGroupDTO dto, @MappingTarget CategoryGroup entity);
}
