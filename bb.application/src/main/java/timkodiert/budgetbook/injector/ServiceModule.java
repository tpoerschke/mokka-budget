package timkodiert.budgetbook.injector;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import jakarta.validation.MessageInterpolator;
import javafx.fxml.FXMLLoader;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import timkodiert.budgetbook.analysis.AnalysisService;
import timkodiert.budgetbook.analysis.AnalysisServiceImpl;
import timkodiert.budgetbook.analysis.CategorySeriesGenerator;
import timkodiert.budgetbook.analysis.CategorySeriesGeneratorImpl;
import timkodiert.budgetbook.annual_overview.AnnualOverviewService;
import timkodiert.budgetbook.annual_overview.AnnualOverviewServiceImpl;
import timkodiert.budgetbook.budget.BudgetService;
import timkodiert.budgetbook.budget.BudgetServiceImpl;
import timkodiert.budgetbook.chart.ExpenseBreakdownService;
import timkodiert.budgetbook.chart.ExpenseBreakdownServiceImpl;
import timkodiert.budgetbook.chart.ExpenseTrendService;
import timkodiert.budgetbook.chart.ExpenseTrendServiceImpl;
import timkodiert.budgetbook.crud.BillingCrudServiceImpl;
import timkodiert.budgetbook.crud.CategoryCrudServiceImpl;
import timkodiert.budgetbook.crud.CategoryGroupCrudServiceImpl;
import timkodiert.budgetbook.crud.FixedTurnoverCrudServiceImpl;
import timkodiert.budgetbook.crud.UniqueTurnoverCrudServiceImpl;
import timkodiert.budgetbook.domain.BillingCrudService;
import timkodiert.budgetbook.domain.CategoryCrudService;
import timkodiert.budgetbook.domain.CategoryGroupCrudService;
import timkodiert.budgetbook.domain.FixedTurnoverCrudService;
import timkodiert.budgetbook.domain.UniqueTurnoverCrudService;
import timkodiert.budgetbook.i18n.LanguageManager;
import timkodiert.budgetbook.importer.TurnoverImporter;
import timkodiert.budgetbook.importer.TurnoverImporterImpl;
import timkodiert.budgetbook.monthly_overview.MonthlyOverviewService;
import timkodiert.budgetbook.monthly_overview.MonthlyOverviewServiceImpl;
import timkodiert.budgetbook.properties.PropertiesService;
import timkodiert.budgetbook.properties.PropertiesServiceImpl;
import timkodiert.budgetbook.view.BbFxmlLoader;
import timkodiert.budgetbook.view.MainView;
import timkodiert.budgetbook.view.MainViewImpl;

// @formatter:off
@Module
public interface ServiceModule {
    @Binds @Singleton MainView bindMainView(MainViewImpl impl);

    @Binds @Singleton PropertiesService bindPropertiesService(PropertiesServiceImpl impl);

    @Binds FXMLLoader bindFXMLLoader(BbFxmlLoader impl);

    @Provides @Singleton static MessageInterpolator provideMessageInterpolator(LanguageManager languageManager) {
        return new ResourceBundleMessageInterpolator(LanguageManager.AVAILABLE_LOCALES, LanguageManager.DEFAULT_LOCALE, languageManager, false);
    }

    // Business Logic (ggf. in eigenes Injector-Modul auslagern)
    @Binds CategorySeriesGenerator bindCategorySeriesGenerator(CategorySeriesGeneratorImpl impl);
    @Binds TurnoverImporter bindTurnoverImporter(TurnoverImporterImpl impl);

    @Binds FixedTurnoverCrudService bindFixedTurnoverCrudService(FixedTurnoverCrudServiceImpl impl);
    @Binds UniqueTurnoverCrudService bindUniqueTurnoverCrudService(UniqueTurnoverCrudServiceImpl impl);
    @Binds CategoryCrudService bindCategoryCrudService(CategoryCrudServiceImpl impl);
    @Binds CategoryGroupCrudService bindCategoryGroupCrudService(CategoryGroupCrudServiceImpl impl);
    @Binds BillingCrudService bindBillingCrudService(BillingCrudServiceImpl impl);

    @Binds MonthlyOverviewService bindMonthlyOverviewService(MonthlyOverviewServiceImpl impl);
    @Binds BudgetService bindBudgetService(BudgetServiceImpl impl);
    @Binds ExpenseBreakdownService bindExpenseBreakdownService(ExpenseBreakdownServiceImpl impl);
    @Binds ExpenseTrendService bindExpenseTrendService(ExpenseTrendServiceImpl impl);
    @Binds AnnualOverviewService bindAnnualOverviewService(AnnualOverviewServiceImpl impl);
    @Binds AnalysisService bindAnalysisService(AnalysisServiceImpl impl);
}
// @formatter:on
