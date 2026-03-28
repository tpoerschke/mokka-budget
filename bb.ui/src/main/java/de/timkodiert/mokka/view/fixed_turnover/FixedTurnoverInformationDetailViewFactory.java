package de.timkodiert.mokka.view.fixed_turnover;

import java.util.function.BiConsumer;

import dagger.assisted.AssistedFactory;

import de.timkodiert.mokka.domain.PaymentInformationDTO;

@AssistedFactory
public interface FixedTurnoverInformationDetailViewFactory {

    FixedTurnoverInformationDetailView create(BiConsumer<PaymentInformationDTO, PaymentInformationDTO> updateCallback);
}
