package de.timkodiert.mokka.crud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;
import de.timkodiert.mokka.domain.repository.CategoryGroupsRepository;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.Repository;

public class ReferenceResolver {

    private final Map<Class<?>, Repository<?>> repositoryMap = new HashMap<>();

    @Inject
    public ReferenceResolver(CategoryGroupsRepository categoryGroupsRepository,
                             CategoriesRepository categoriesRepository,
                             FixedExpensesRepository fixedExpensesRepository) {
        repositoryMap.put(CategoryGroupDTO.class, categoryGroupsRepository);
        repositoryMap.put(CategoryDTO.class, categoriesRepository);
        repositoryMap.put(FixedTurnoverDTO.class, fixedExpensesRepository);
    }

    public <R, T> T resolve(Reference<R> reference) {
        if (reference == null) {
            return null;
        }
        return loadEntityForReference(reference);
    }

    public <R, T> List<T> resolve(List<Reference<R>> references) {
        return (List<T>) references.stream().map(this::loadEntityForReference).toList();
    }

    private <R, T> T loadEntityForReference(Reference<R> reference) {
        return (T) repositoryMap.get(reference.refClass()).findById(reference.id());
    }
}
