package de.timkodiert.mokka.domain;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportRuleDTO {

    private int id;
    private boolean active;
    private String receiverContains;
    private String referenceContains;

    public ImportRuleDTO() {
        id = new Random().nextInt(Integer.MIN_VALUE, 0);
        active = true;
    }

    public static ImportRuleDTO create(boolean active, String receiverContains, String referenceContains) {
        ImportRuleDTO dto = new ImportRuleDTO();
        dto.setActive(active);
        dto.setReceiverContains(receiverContains);
        dto.setReferenceContains(referenceContains);
        return dto;
    }
}
