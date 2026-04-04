package de.timkodiert.mokka.domain;

import java.util.List;

import org.jspecify.annotations.Nullable;

public interface UniqueTurnoverCrudService {

    ManageViewContainer<UniqueTurnoverDTO> readAll(int page, @Nullable Reference<FixedTurnoverDTO> fixedTurnoverRef);
    List<SimplifiedUniqueTurnoverDTO> readSortedByDateDesc(int limit);
    UniqueTurnoverDTO readById(int id);

    boolean create(UniqueTurnoverDTO uniqueTurnoverDTO);
    boolean update(UniqueTurnoverDTO uniqueTurnoverDTO);
    boolean delete(int id);

    List<String> getUniqueTurnoverLabels();
    List<String> getUniqueTurnoverInformationLabels();
}
