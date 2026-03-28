package de.timkodiert.mokka.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.budget.BudgetType;


@Getter
@Setter
public class CategoryDTO {

    private int id;
    @NonNull
    @NotBlank(message = "{category.name.notBlank}")
    private String name;
    private String description;
    private @Nullable Reference<CategoryGroupDTO> group;

    private boolean hasLinkedTurnover;

    private boolean budgetActive;
    private @Nullable Integer budgetValue;
    private @Nullable BudgetType budgetType;

    public boolean isNew() {
        return id <= 0;
    }
}
