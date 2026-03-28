package de.timkodiert.mokka.crud;

import java.util.List;
import java.util.Objects;

import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;
import org.mapstruct.factory.Mappers;

import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.SimplifiedUniqueTurnoverDTO;
import de.timkodiert.mokka.domain.UniqueTurnoverCrudService;
import de.timkodiert.mokka.domain.UniqueTurnoverDTO;
import de.timkodiert.mokka.domain.model.BaseEntity;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.UniqueExpenseInformationRepository;
import de.timkodiert.mokka.domain.repository.UniqueExpensesRepository;
import de.timkodiert.mokka.representation.RowType;

import static de.timkodiert.mokka.util.ObjectUtils.nvl;

public class UniqueTurnoverCrudServiceImpl implements UniqueTurnoverCrudService {

    private final ReferenceResolver referenceResolver;
    private final UniqueExpensesRepository uniqueTurnoverRepository;
    private final UniqueExpenseInformationRepository uniqueTurnoverInformationRepository;

    @Inject
    public UniqueTurnoverCrudServiceImpl(ReferenceResolver referenceResolver,
                                         UniqueExpensesRepository uniqueTurnoverRepository,
                                         UniqueExpenseInformationRepository uniqueTurnoverInformationRepository) {
        this.referenceResolver = referenceResolver;
        this.uniqueTurnoverRepository = uniqueTurnoverRepository;
        this.uniqueTurnoverInformationRepository = uniqueTurnoverInformationRepository;
    }

    @Override
    public List<UniqueTurnoverDTO> readAll(@Nullable Reference<FixedTurnoverDTO> fixedTurnoverRef) {
        UniqueTurnoverMapper mapper = Mappers.getMapper(UniqueTurnoverMapper.class);
        return uniqueTurnoverRepository.findAll()
                                       .stream()
                                       .filter(uq -> Objects.equals(nvl(uq.getFixedTurnover(), BaseEntity::getId), nvl(fixedTurnoverRef, Reference::id)))
                                       .map(mapper::uniqueTurnoverToUniqueTurnoverDto)
                                       .toList();
    }

    @Override
    public List<SimplifiedUniqueTurnoverDTO> readSortedByDateDesc(int limit) {
        return uniqueTurnoverRepository.findByLimitSortedByDateDesc(limit)
                                       .stream()
                                       .map(ut -> new SimplifiedUniqueTurnoverDTO(ut.getId(), ut.getBiller(), ut.getDate(), ut.getTotalValue(), RowType.DEFAULT))
                                       .toList();
    }

    @Override
    public UniqueTurnoverDTO readById(int id) {
        UniqueTurnoverMapper mapper = Mappers.getMapper(UniqueTurnoverMapper.class);
        return mapper.uniqueTurnoverToUniqueTurnoverDto(uniqueTurnoverRepository.findById(id));
    }

    @Override
    public boolean create(UniqueTurnoverDTO uniqueTurnoverDTO) {
        UniqueTurnover uniqueTurnover = new UniqueTurnover();
        UniqueTurnoverMapper uniqueTurnoverMapper = Mappers.getMapper(UniqueTurnoverMapper.class);
        uniqueTurnoverMapper.updateUniqueTurnover(uniqueTurnoverDTO, uniqueTurnover, referenceResolver);
        linkTurnoverInformation(uniqueTurnover);
        uniqueTurnoverRepository.merge(uniqueTurnover);
        return true;
    }

    @Override
    public boolean update(UniqueTurnoverDTO uniqueTurnoverDTO) {
        UniqueTurnover uniqueTurnover = uniqueTurnoverRepository.findById(uniqueTurnoverDTO.getId());
        UniqueTurnoverMapper uniqueTurnoverMapper = Mappers.getMapper(UniqueTurnoverMapper.class);
        uniqueTurnoverMapper.updateUniqueTurnover(uniqueTurnoverDTO, uniqueTurnover, referenceResolver);
        linkTurnoverInformation(uniqueTurnover);
        uniqueTurnoverRepository.merge(uniqueTurnover);
        return true;
    }

    @Override
    public boolean delete(int id) {
        UniqueTurnover uniqueTurnover = uniqueTurnoverRepository.findById(id);
        if (uniqueTurnover == null) {
            return false;
        }
        uniqueTurnoverRepository.remove(uniqueTurnover);
        return true;
    }

    private void linkTurnoverInformation(UniqueTurnover uniqueTurnover) {
        uniqueTurnover.getPaymentInformations().forEach(info -> {
            info.setExpense(uniqueTurnover);
            if (info.getId() < 0) {
                info.setId(0);
            }
        });
        uniqueTurnover.getPaymentInformations().forEach(info -> {
            Category category = info.getCategory();
            if (category == null) {
                return;
            }
            boolean notYetLinked = category.getUniqueExpenseInformation().stream().noneMatch(i -> i.getId() == info.getId());
            if (notYetLinked) {
                category.getUniqueExpenseInformation().add(info);
            }
        });
    }

    @Override
    public List<String> getUniqueTurnoverLabels() {
        return uniqueTurnoverRepository.findAll().stream().map(UniqueTurnover::getBiller).distinct().toList();
    }

    @Override
    public List<String> getUniqueTurnoverInformationLabels() {
        return uniqueTurnoverInformationRepository.findAll().stream().map(UniqueTurnoverInformation::getLabel).distinct().toList();
    }
}
