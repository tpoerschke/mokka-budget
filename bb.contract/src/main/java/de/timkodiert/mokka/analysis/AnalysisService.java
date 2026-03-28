package de.timkodiert.mokka.analysis;

import java.time.YearMonth;
import java.util.List;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;

public interface AnalysisService {

    List<TableRowData> getTurnoverList(Reference<CategoryDTO> categoryRef, YearMonth yearMonth);
}
