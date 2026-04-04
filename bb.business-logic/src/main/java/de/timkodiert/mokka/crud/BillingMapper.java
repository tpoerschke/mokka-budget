package de.timkodiert.mokka.crud;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import de.timkodiert.mokka.domain.BillingDTO;
import de.timkodiert.mokka.domain.SimplifiedUniqueTurnoverDTO;
import de.timkodiert.mokka.domain.model.Billing;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.repository.UniqueTurnoverRepository;
import de.timkodiert.mokka.representation.RowType;

@Mapper
public interface BillingMapper {

    @Mapping(target = "uniqueTurnovers", source = "billing")
    BillingDTO billingToDto(Billing billing);

    @Mapping(target = "uniqueTurnovers", source = "dto")
    void updateBilling(BillingDTO dto, @MappingTarget Billing entity, @Context UniqueTurnoverRepository uniqueTurnoverRepository);

    default List<SimplifiedUniqueTurnoverDTO> mapUniqueTurnovers(Billing billing) {
        return billing.getUniqueTurnovers()
                      .stream()
                      .map(ut -> new SimplifiedUniqueTurnoverDTO(ut.getId(), ut.getBiller(), ut.getDate(), ut.getTotalValue(), RowType.DEFAULT))
                      .toList();
    }

    default List<UniqueTurnover> mapUniqueTurnovers(BillingDTO dto, @Context UniqueTurnoverRepository uniqueTurnoverRepository) {
        return dto.getUniqueTurnovers().stream().map(SimplifiedUniqueTurnoverDTO::getId).map(uniqueTurnoverRepository::findById).toList();
    }
}
