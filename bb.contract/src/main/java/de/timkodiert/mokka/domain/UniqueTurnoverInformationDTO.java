package de.timkodiert.mokka.domain;

import java.io.Serializable;
import java.util.Random;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniqueTurnoverInformationDTO implements Serializable {

    private int id;
    @NotBlank(message = "{attribute.notNull}")
    private String label;
    private int value;
    @NotNull(message = "{attribute.notNull}")
    private TurnoverDirection direction;
    private Reference<CategoryDTO> category;

    public UniqueTurnoverInformationDTO() {
        id = new Random().nextInt(Integer.MIN_VALUE, 0);
    }

    public int getValueSigned() {
        return value * direction.getSign();
    }
}
