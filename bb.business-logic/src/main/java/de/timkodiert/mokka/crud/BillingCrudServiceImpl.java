package de.timkodiert.mokka.crud;

import java.util.List;

import jakarta.inject.Inject;
import org.mapstruct.factory.Mappers;

import de.timkodiert.mokka.domain.BillingCrudService;
import de.timkodiert.mokka.domain.BillingDTO;
import de.timkodiert.mokka.domain.model.Billing;
import de.timkodiert.mokka.domain.repository.Repository;
import de.timkodiert.mokka.domain.repository.UniqueExpensesRepository;

public class BillingCrudServiceImpl implements BillingCrudService {

    private final Repository<Billing> billingRepository;
    private final UniqueExpensesRepository uniqueTurnoverRepository;

    @Inject
    public BillingCrudServiceImpl(Repository<Billing> billingRepository, UniqueExpensesRepository uniqueTurnoverRepository) {
        this.billingRepository = billingRepository;
        this.uniqueTurnoverRepository = uniqueTurnoverRepository;
    }

    @Override
    public List<BillingDTO> readAll() {
        BillingMapper mapper = Mappers.getMapper(BillingMapper.class);
        return billingRepository.findAll().stream().map(mapper::billingToDto).toList();
    }

    @Override
    public BillingDTO readById(int id) {
        BillingMapper mapper = Mappers.getMapper(BillingMapper.class);
        return mapper.billingToDto(billingRepository.findById(id));
    }

    @Override
    public boolean create(BillingDTO billingDTO) {
        Billing billing = new Billing();
        BillingMapper mapper = Mappers.getMapper(BillingMapper.class);
        mapper.updateBilling(billingDTO, billing, uniqueTurnoverRepository);
        billingRepository.persist(billing);
        return true;
    }

    @Override
    public boolean update(BillingDTO billingDTO) {
        Billing billing = billingRepository.findById(billingDTO.getId());
        BillingMapper mapper = Mappers.getMapper(BillingMapper.class);
        mapper.updateBilling(billingDTO, billing, uniqueTurnoverRepository);
        billingRepository.merge(billing);
        return true;
    }

    @Override
    public boolean delete(int id) {
        Billing billing = billingRepository.findById(id);
        if (billing == null) {
            return false;
        }
        billingRepository.remove(billing);
        return true;
    }
}
