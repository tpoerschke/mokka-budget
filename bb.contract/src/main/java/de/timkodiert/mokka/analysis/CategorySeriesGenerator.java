package de.timkodiert.mokka.analysis;

import java.util.List;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;

public interface CategorySeriesGenerator {
    List<Double> generateCumulativeCategorySeries(AnalysisPeriod period, Reference<CategoryDTO> categoryRef);
    List<Double> generateCategorySeries(AnalysisPeriod period, Reference<CategoryDTO> category);
}
