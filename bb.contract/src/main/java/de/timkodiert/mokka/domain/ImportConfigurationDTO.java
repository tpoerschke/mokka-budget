package de.timkodiert.mokka.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import de.timkodiert.mokka.importer.CsvEncoding;

@Setter
@Getter
public class ImportConfigurationDTO {

    private int id = 0;

    @NotBlank(message = "{importConfiguration.name.notBlank}")
    private String name;

    @NotNull(message = "{attribute.notNull}")
    private Integer skipLines;

    @NotNull(message = "{attribute.notNull}")
    private CsvEncoding encoding;

    @NotNull(message = "{attribute.notNull}")
    private Integer columnDate;

    @NotNull(message = "{attribute.notNull}")
    private Integer columnReceiver;

    @NotNull(message = "{attribute.notNull}")
    private Integer columnPostingText;

    @NotNull(message = "{attribute.notNull}")
    private Integer columnReference;

    @NotNull(message = "{attribute.notNull}")
    private Integer columnAmount;

    public boolean isNew() {
        return id <= 0;
    }
}
