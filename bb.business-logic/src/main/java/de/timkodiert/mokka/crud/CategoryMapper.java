package de.timkodiert.mokka.crud;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.Category;

@Mapper
public abstract class CategoryMapper {

    @Mapping(target = "group", source = "category")
    @Mapping(target = "hasLinkedTurnover", source = "category")
    abstract CategoryDTO categoryToDto(Category category);

    protected Reference<CategoryGroupDTO> mapGroup(Category category) {
        if (category.getGroup() == null) {
            return null;
        }
        return new Reference<>(CategoryGroupDTO.class, category.getGroup().getId(), category.getGroup().getName());
    }

    protected boolean mapHasLinkedTurnover(Category category) {
        return !category.getFixedExpenses().isEmpty() || !category.getUniqueTurnoverInformation().isEmpty();
    }

    @Mapping(target = "group", expression = "java(referenceResolver.resolve(dto.getGroup()))")
    abstract void updateCategory(CategoryDTO dto, @MappingTarget Category entity, @Context ReferenceResolver referenceResolver);
}
