package de.timkodiert.mokka.domain;

import java.util.List;

public interface CategorizableDTO {
    List<Reference<CategoryDTO>> getCategories();
}
