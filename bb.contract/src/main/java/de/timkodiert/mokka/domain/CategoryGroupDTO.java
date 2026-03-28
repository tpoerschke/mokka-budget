package de.timkodiert.mokka.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryGroupDTO {

    private int id = 0;
    @NotBlank(message = "{categoryGroup.name.notBlank}")
    private String name;
    private String description;

    private List<Reference<CategoryDTO>> categories = new ArrayList<>();

    public boolean isNew() {
        return id <= 0;
    }
}
