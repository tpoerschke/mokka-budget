package de.timkodiert.mokka.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Billing extends BaseEntity {

    @Setter
    @NotBlank(message = "Der Titel muss angegeben werden.")
    @Column(nullable = false)
    private String title;

    @Setter
    private String description;

    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "billing_unique_turnover",
            joinColumns = @JoinColumn(name = "billing_id"),
            inverseJoinColumns = @JoinColumn(name = "unique_turnover_id")
    )
    private List<UniqueTurnover> uniqueTurnovers = new ArrayList<>();

}
