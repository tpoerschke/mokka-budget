package de.timkodiert.mokka.domain;

import java.util.List;

public interface CategoryCrudService {

    List<Reference<CategoryDTO>> readAllAsReference();
    List<CategoryDTO> readAll();
    CategoryDTO readById(int id);

    boolean create(CategoryDTO categoryDTO);
    boolean update(CategoryDTO categoryDTO);
    boolean delete(int id);
}
