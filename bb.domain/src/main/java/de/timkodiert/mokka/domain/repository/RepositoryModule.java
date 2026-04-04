package de.timkodiert.mokka.domain.repository;

import dagger.Binds;
import dagger.Module;

import de.timkodiert.mokka.domain.model.AccountTurnover;
import de.timkodiert.mokka.domain.model.Billing;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.CategoryGroup;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.ImportRule;
import de.timkodiert.mokka.domain.model.PaymentInformation;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;

@Module
public interface RepositoryModule {

    @Binds
    Repository<Category> provideCategoriesRepository(CategoriesRepository impl);

    @Binds
    Repository<CategoryGroup> provideCategoryGroupsRepository(CategoryGroupsRepository impl);

    @Binds
    Repository<FixedTurnover> provideFixedExpensesRepository(FixedExpensesRepository impl);

    @Binds
    Repository<PaymentInformation> providePaymentInformationsRepository(PaymentInformationsRepository impl);

    @Binds
    Repository<UniqueTurnover> provideUniqueExpensesRepository(UniqueTurnoverRepository impl);

    @Binds
    Repository<UniqueTurnoverInformation> provideUniqueExpenseInformationRepository(UniqueExpenseInformationRepository impl);

    @Binds
    Repository<AccountTurnover> provideAccountTurnoverRepository(AccountTurnoverRepository impl);

    @Binds
    Repository<ImportRule> provideImportRulesRepository(ImportRulesRepository impl);

    @Binds
    Repository<Billing> provideBillingRepository(BillingRepository impl);
}
