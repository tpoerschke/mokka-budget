package de.timkodiert.mokka.crud;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import de.timkodiert.mokka.domain.AccountTurnoverDTO;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.PaymentInformationDTO;
import de.timkodiert.mokka.domain.PaymentType;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.AccountTurnover;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.PaymentInformation;

@Mapper
public interface FixedTurnoverMapper {

    @Mapping(target = "category", source = "fixedTurnover")
    @Mapping(target = "paymentType", source = "fixedTurnover")
    @Mapping(target = "accountTurnover", source = "fixedTurnover")
    FixedTurnoverDTO fixedTurnoverToFixedTurnoverDto(FixedTurnover fixedTurnover);

    default PaymentType mapPaymentType(FixedTurnover fixedTurnover) {
        return fixedTurnover.getType();
    }

    default Reference<CategoryDTO> mapCategory(FixedTurnover fixedTurnover) {
        Category category = fixedTurnover.getCategory();
        if (category == null) {
            return null;
        }
        return new Reference<>(CategoryDTO.class, category.getId(), category.getName());
    }

    default List<AccountTurnoverDTO> mapAccountTurnovers(FixedTurnover fixedTurnover) {
        return fixedTurnover.getImports().stream().sorted().map(this::accountTurnoverToDto).toList();
    }

    PaymentInformationDTO paymentInformationToPaymentInformationDto(PaymentInformation paymentInformation);

    default YearMonth map(MonthYear value) {
        return Optional.ofNullable(value).map(MonthYear::asYearMonth).orElse(null);
    }

    AccountTurnoverDTO accountTurnoverToDto(AccountTurnover accountTurnover);

    @Mapping(target = "category", expression = "java(referenceResolver.resolve(dto.getCategory()))")
    void updateFixedTurnover(FixedTurnoverDTO dto, @MappingTarget FixedTurnover fixedTurnover, @Context ReferenceResolver referenceResolver);

    default MonthYear map(YearMonth value) {
        return Optional.ofNullable(value).map(MonthYear::of).orElse(null);
    }
}
