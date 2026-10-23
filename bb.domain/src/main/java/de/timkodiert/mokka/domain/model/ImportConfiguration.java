package de.timkodiert.mokka.domain.model;

import java.util.stream.IntStream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import de.timkodiert.mokka.importer.account_turnover.CsvEncoding;

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
    private int columnDate;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int columnReceiver;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int columnPostingText;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int columnReference;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int columnAmount;

    @Setter
    @Column(name = "default", nullable = false)
    private boolean isDefault;

    public int getMaxColumnIndex() {
        return IntStream.of(columnDate, columnReceiver, columnPostingText, columnReference, getColumnAmount()).max().orElseThrow() - 1;
    }
}
