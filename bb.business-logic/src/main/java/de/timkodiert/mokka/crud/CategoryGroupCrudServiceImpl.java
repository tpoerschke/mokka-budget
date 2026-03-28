package de.timkodiert.mokka.crud;

import java.util.List;

import jakarta.inject.Inject;
import org.mapstruct.factory.Mappers;

import de.timkodiert.mokka.domain.CategoryGroupCrudService;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.model.CategoryGroup;
import de.timkodiert.mokka.domain.repository.CategoryGroupsRepository;

public class CategoryGroupCrudServiceImpl implements CategoryGroupCrudService {

    private final CategoryGroupsRepository categoryGroupsRepository;

    @Inject
    public CategoryGroupCrudServiceImpl(CategoryGroupsRepository categoryGroupsRepository) {
        this.categoryGroupsRepository = categoryGroupsRepository;
    }

    @Override
    public List<CategoryGroupDTO> readAll() {
        CategoryGroupMapper categoryGroupMapper = Mappers.getMapper(CategoryGroupMapper.class);
        return categoryGroupsRepository.findAll().stream().map(categoryGroupMapper::categoryGroupToDto).toList();
    }

    @Override
    public CategoryGroupDTO readById(int id) {
        CategoryGroupMapper categoryGroupMapper = Mappers.getMapper(CategoryGroupMapper.class);
        return categoryGroupMapper.categoryGroupToDto(categoryGroupsRepository.findById(id));
    }

    @Override
    public boolean create(CategoryGroupDTO categoryGroupDTO) {
        CategoryGroup newCategoryGroup = new CategoryGroup();
        CategoryGroupMapper categoryGroupMapper = Mappers.getMapper(CategoryGroupMapper.class);
        categoryGroupMapper.updateCategoryGroup(categoryGroupDTO, newCategoryGroup);
        categoryGroupsRepository.persist(newCategoryGroup);
        return true;
    }

    @Override
    public boolean update(CategoryGroupDTO categoryGroupDTO) {
        CategoryGroup categoryGroup = categoryGroupsRepository.findById(categoryGroupDTO.getId());
        CategoryGroupMapper categoryGroupMapper = Mappers.getMapper(CategoryGroupMapper.class);
        categoryGroupMapper.updateCategoryGroup(categoryGroupDTO, categoryGroup);
        categoryGroupsRepository.persist(categoryGroup);
        return true;
    }

    @Override
    public boolean delete(int id) {
        CategoryGroup categoryGroup = categoryGroupsRepository.findById(id);
        if (categoryGroup == null) {
            return false;
        }
        categoryGroupsRepository.remove(categoryGroup);
        return true;
    }
}
