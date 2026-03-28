package de.timkodiert.mokka.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class CategoryGroup extends BaseEntity {

    @Setter
    @NotEmpty
    private String name;

    @Setter
    private String description;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final Set<Category> categories = new HashSet<>();

}
