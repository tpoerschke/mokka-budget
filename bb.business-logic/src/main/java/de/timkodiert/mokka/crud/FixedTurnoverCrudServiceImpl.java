package de.timkodiert.mokka.crud;

import java.util.List;

import jakarta.inject.Inject;
import org.mapstruct.factory.Mappers;

import de.timkodiert.mokka.domain.FixedTurnoverCrudService;
import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.repository.Repository;

public class FixedTurnoverCrudServiceImpl implements FixedTurnoverCrudService {

    private final ReferenceResolver referenceResolver;
    private final Repository<FixedTurnover> fixedTurnoverRepository;

    @Inject
    public FixedTurnoverCrudServiceImpl(ReferenceResolver referenceResolver, Repository<FixedTurnover> fixedTurnoverRepository) {
        this.referenceResolver = referenceResolver;
        this.fixedTurnoverRepository = fixedTurnoverRepository;
    }

    @Override
    public List<Reference<FixedTurnoverDTO>> findAllAsReference() {
        return fixedTurnoverRepository.findAll().stream().map(t -> new Reference<>(FixedTurnoverDTO.class, t.getId(), t.getPosition())).toList();
    }

    @Override
    public List<FixedTurnoverDTO> readAll() {
        FixedTurnoverMapper mapper = Mappers.getMapper(FixedTurnoverMapper.class);
        return fixedTurnoverRepository.findAll().stream().map(mapper::fixedTurnoverToFixedTurnoverDto).toList();
    }

    @Override
    public FixedTurnoverDTO readById(int id) {
        FixedTurnoverMapper mapper = Mappers.getMapper(FixedTurnoverMapper.class);
        return fixedTurnoverRepository.findAll().stream().filter(t -> t.getId() == id).findAny().map(mapper::fixedTurnoverToFixedTurnoverDto).orElse(null);
    }

    @Override
    public int create(FixedTurnoverDTO fixedTurnoverDTO) {
        FixedTurnover fixedTurnover = new FixedTurnover();
        FixedTurnoverMapper fixedTurnoverMapper = Mappers.getMapper(FixedTurnoverMapper.class);
        fixedTurnoverMapper.updateFixedTurnover(fixedTurnoverDTO, fixedTurnover, referenceResolver);
        linkSubEntities(fixedTurnover);
        fixedTurnover = fixedTurnoverRepository.merge(fixedTurnover);
        return fixedTurnover.getId();
    }

    @Override
    public boolean update(FixedTurnoverDTO fixedTurnoverDTO) {
        FixedTurnover fixedTurnover = fixedTurnoverRepository.findById(fixedTurnoverDTO.getId());
        FixedTurnoverMapper fixedTurnoverMapper = Mappers.getMapper(FixedTurnoverMapper.class);
        fixedTurnoverMapper.updateFixedTurnover(fixedTurnoverDTO, fixedTurnover, referenceResolver);
        linkSubEntities(fixedTurnover);
        fixedTurnoverRepository.merge(fixedTurnover);
        return true;
    }

    @Override
    public boolean delete(int id) {
        FixedTurnover fixedTurnover = fixedTurnoverRepository.findById(id);
        if (fixedTurnover == null) {
            return false;
        }
        fixedTurnoverRepository.remove(fixedTurnover);
        return true;
    }

    private void linkSubEntities(FixedTurnover fixedTurnover) {
        fixedTurnover.getPaymentInformations().forEach(payInfo -> {
            payInfo.setExpense(fixedTurnover);
            if (payInfo.getId() < 0) {
                payInfo.setId(0);
            }
        });
        fixedTurnover.getImportRules().forEach(importRule -> {
            if (importRule.getId() < 0) {
                importRule.setId(0);
            }
            importRule.setLinkedFixedExpense(fixedTurnover);
        });
        Category category = fixedTurnover.getCategory();
        boolean notYetLinked = category != null && category.getFixedExpenses().stream().noneMatch(ft -> ft.getId() == fixedTurnover.getId());
        if (notYetLinked) {
            category.getFixedExpenses().add(fixedTurnover);
        }
    }
}
