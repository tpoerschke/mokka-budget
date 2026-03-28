package de.timkodiert.mokka.view.unique_turnover;

import java.util.function.BiConsumer;

import dagger.assisted.AssistedFactory;

import de.timkodiert.mokka.domain.UniqueTurnoverInformationDTO;

@AssistedFactory
public interface UniqueTurnoverInformationDetailViewFactory {
    UniqueExpenseInformationDetailView create(BiConsumer<UniqueTurnoverInformationDTO, UniqueTurnoverInformationDTO> updateCallback);
}
