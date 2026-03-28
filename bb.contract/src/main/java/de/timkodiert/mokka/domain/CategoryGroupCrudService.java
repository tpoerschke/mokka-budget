package de.timkodiert.mokka.domain;

import java.util.List;

public interface CategoryGroupCrudService {

    List<CategoryGroupDTO> readAll();
    CategoryGroupDTO readById(int id);

    boolean create(CategoryGroupDTO categoryGroupDTO);
    boolean update(CategoryGroupDTO categoryGroupDTO);
    boolean delete(int id);
}
