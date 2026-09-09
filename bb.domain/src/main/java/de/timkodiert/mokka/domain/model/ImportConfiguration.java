package de.timkodiert.mokka.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import de.timkodiert.mokka.importer.CsvEncoding;

@Getter
@Entity
public class ImportConfiguration extends BaseEntity {

    @Setter
    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer skipLines;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CsvEncoding encoding;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer columnDate;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer columnReceiver;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer columnPostingText;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer columnReference;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer columnAmount;
}
