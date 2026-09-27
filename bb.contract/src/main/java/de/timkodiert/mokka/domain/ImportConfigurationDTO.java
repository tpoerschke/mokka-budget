package de.timkodiert.mokka.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import de.timkodiert.mokka.importer.CsvEncoding;

@Setter
@Getter
public class ImportConfigurationDTO {

    private int id;

    @NotBlank(message = "{importConfiguration.name.notBlank}")
    private String name;

    private int skipLines;

    @NotNull(message = "{attribute.notNull}")
    private CsvEncoding encoding;

    private int columnDate;
    private int columnReceiver;
    private int columnPostingText;
    private int columnReference;
    private int columnAmount;

    public boolean isNew() {
        return id <= 0;
    }
}
