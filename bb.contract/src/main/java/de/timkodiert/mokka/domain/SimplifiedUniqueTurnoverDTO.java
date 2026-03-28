package de.timkodiert.mokka.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;

@Getter
@AllArgsConstructor
public class SimplifiedUniqueTurnoverDTO implements HasRowType {

    private int id;
    private String biller;
    private LocalDate date;
    private int totalValue;
    private RowType rowType;
    
}
