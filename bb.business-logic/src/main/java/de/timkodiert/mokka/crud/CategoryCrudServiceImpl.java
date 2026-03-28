package de.timkodiert.mokka.crud;

import java.util.List;

import jakarta.inject.Inject;
import org.mapstruct.factory.Mappers;

import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;

public class CategoryCrudServiceImpl implements CategoryCrudService {

    private final CategoriesRepository categoriesRepository;
    private final ReferenceResolver referenceResolver;

    @Inject
    public CategoryCrudServiceImpl(CategoriesRepository categoriesRepository, ReferenceResolver referenceResolver) {
        this.categoriesRepository = categoriesRepository;
        this.referenceResolver = referenceResolver;
    }

    @Override
    public List<Reference<CategoryDTO>> readAllAsReference() {
        return categoriesRepository.findAll().stream().map(category -> new Reference<>(CategoryDTO.class, category.getId(), category.getName())).toList();
    }

    @Override
    public List<CategoryDTO> readAll() {
        CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
        return categoriesRepository.findAll().stream().map(categoryMapper::categoryToDto).toList();
    }

    @Override
    public CategoryDTO readById(int id) {
        CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
        return categoryMapper.categoryToDto(categoriesRepository.findById(id));
    }

    @Override
    public boolean create(CategoryDTO categoryDTO) {
        Category newCategory = new Category();
        CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
        categoryMapper.updateCategory(categoryDTO, newCategory, referenceResolver);
        linkEntities(newCategory);
        categoriesRepository.persist(newCategory);
        return true;
    }

    @Override
    public boolean update(CategoryDTO categoryDTO) {
        Category category = categoriesRepository.findById(categoryDTO.getId());
        CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
        categoryMapper.updateCategory(categoryDTO, category, referenceResolver);
        linkEntities(category);
        categoriesRepository.persist(category);
        return true;
    }

    @Override
    public boolean delete(int id) {
        Category category = categoriesRepository.findById(id);
        if (category == null) {
            return false;
        }
        categoriesRepository.remove(category);
        return true;
    }

    private void linkEntities(Category category) {
        if (category.getGroup() == null) {
            return;
        }
        boolean notYetLinked = category.getGroup().getCategories().stream().noneMatch(c -> c.getId() == category.getId());
        if (notYetLinked) {
            category.getGroup().getCategories().add(category);
        }
    }
}
