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

    private int columnDate = 1;
    private int columnReceiver = 1;
    private int columnPostingText = 1;
    private int columnReference = 1;
    private int columnAmount = 1;
    private boolean isDefault;

    public boolean isNew() {
        return id <= 0;
    }
}
