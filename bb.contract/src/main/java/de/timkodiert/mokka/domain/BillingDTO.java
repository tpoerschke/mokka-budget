package de.timkodiert.mokka.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;

@Getter
@Setter
public class BillingDTO implements HasRowType {

    private int id = 0;
    @NotBlank(message = "Der Titel muss angegeben werden.")
    private String title;
    @NotBlank(message = "Die Beschreibung muss angegeben werden.")
    private String description;
    private List<SimplifiedUniqueTurnoverDTO> uniqueTurnovers = new ArrayList<>();

    public boolean isNew() {
        return id <= 0;
    }

    public int getTotalValue() {
        return uniqueTurnovers.stream().mapToInt(SimplifiedUniqueTurnoverDTO::getTotalValue).sum();
    }

    public @Nullable LocalDate getFirstTurnoverDate() {
        return uniqueTurnovers.stream().map(SimplifiedUniqueTurnoverDTO::getDate).min(Comparator.naturalOrder()).orElse(null);
    }

    public @Nullable LocalDate getLastTurnoverDate() {
        return uniqueTurnovers.stream().map(SimplifiedUniqueTurnoverDTO::getDate).max(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public RowType getRowType() {
        return RowType.DEFAULT;
    }
}
