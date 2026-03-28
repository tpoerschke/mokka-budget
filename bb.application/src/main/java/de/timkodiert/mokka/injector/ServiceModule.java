package de.timkodiert.mokka.injector;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import jakarta.validation.MessageInterpolator;
import javafx.fxml.FXMLLoader;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import de.timkodiert.mokka.analysis.AnalysisService;
import de.timkodiert.mokka.analysis.AnalysisServiceImpl;
import de.timkodiert.mokka.analysis.CategorySeriesGenerator;
import de.timkodiert.mokka.analysis.CategorySeriesGeneratorImpl;
import de.timkodiert.mokka.annual_overview.AnnualOverviewService;
import de.timkodiert.mokka.annual_overview.AnnualOverviewServiceImpl;
import de.timkodiert.mokka.budget.BudgetService;
import de.timkodiert.mokka.budget.BudgetServiceImpl;
import de.timkodiert.mokka.chart.ExpenseBreakdownService;
import de.timkodiert.mokka.chart.ExpenseBreakdownServiceImpl;
import de.timkodiert.mokka.chart.ExpenseTrendService;
import de.timkodiert.mokka.chart.ExpenseTrendServiceImpl;
import de.timkodiert.mokka.crud.BillingCrudServiceImpl;
import de.timkodiert.mokka.crud.CategoryCrudServiceImpl;
import de.timkodiert.mokka.crud.CategoryGroupCrudServiceImpl;
import de.timkodiert.mokka.crud.FixedTurnoverCrudServiceImpl;
import de.timkodiert.mokka.crud.UniqueTurnoverCrudServiceImpl;
import de.timkodiert.mokka.domain.BillingCrudService;
import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryGroupCrudService;
import de.timkodiert.mokka.domain.FixedTurnoverCrudService;
import de.timkodiert.mokka.domain.UniqueTurnoverCrudService;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.importer.TurnoverImporter;
import de.timkodiert.mokka.importer.TurnoverImporterImpl;
import de.timkodiert.mokka.monthly_overview.MonthlyOverviewService;
import de.timkodiert.mokka.monthly_overview.MonthlyOverviewServiceImpl;
import de.timkodiert.mokka.properties.PropertiesService;
import de.timkodiert.mokka.properties.PropertiesServiceImpl;
import de.timkodiert.mokka.view.BbFxmlLoader;
import de.timkodiert.mokka.view.MainView;
import de.timkodiert.mokka.view.MainViewImpl;

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
