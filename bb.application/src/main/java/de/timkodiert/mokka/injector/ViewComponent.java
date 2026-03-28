package de.timkodiert.mokka.injector;

import dagger.Component;
import jakarta.inject.Singleton;

import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.db.MigrationService;
import de.timkodiert.mokka.db.MigrationView;
import de.timkodiert.mokka.domain.repository.RepositoryModule;
import de.timkodiert.mokka.exception.BbUncaughtExceptionHandler;
import de.timkodiert.mokka.properties.PropertiesServiceImpl;
import de.timkodiert.mokka.view.AnnualOverviewView;
import de.timkodiert.mokka.view.MainViewImpl;
import de.timkodiert.mokka.view.MonthlyOverviewView;
import de.timkodiert.mokka.view.about.AboutView;
import de.timkodiert.mokka.view.analysis.AnalysisView;
import de.timkodiert.mokka.view.billing.BillingDetailView;
import de.timkodiert.mokka.view.billing.BillingManageView;
import de.timkodiert.mokka.view.category.CategoriesManageView;
import de.timkodiert.mokka.view.category.CategoryDetailView;
import de.timkodiert.mokka.view.category_group.CategoryGroupDetailView;
import de.timkodiert.mokka.view.category_group.CategoryGroupManageView;
import de.timkodiert.mokka.view.fixed_turnover.FixedTurnoverDetailView;
import de.timkodiert.mokka.view.fixed_turnover.FixedTurnoverManageView;
import de.timkodiert.mokka.view.importer.FixedTurnoverWizardView;
import de.timkodiert.mokka.view.importer.ImportView;
import de.timkodiert.mokka.view.unique_turnover.UniqueTurnoverDetailView;
import de.timkodiert.mokka.view.unique_turnover.UniqueTurnoverManageView;
import de.timkodiert.mokka.view.widget.BudgetWidget;

@Singleton
@Component(modules = {RepositoryModule.class, ServiceModule.class, AppModule.class, DbPathModule.class})
public interface ViewComponent {
    MainViewImpl getMainView();
    AboutView getAboutView();

    // -----------------------------------
    // Übersichten Ausgaben
    // -----------------------------------
    AnnualOverviewView getAnnualOverviewView();
    MonthlyOverviewView getMonthlyOverview();
    BudgetWidget getBudgetWidget();

    // -----------------------------------
    // Analyse
    // -----------------------------------
    AnalysisView getAnalysisView();

    // -----------------------------------
    // Kategorien Ausgaben
    // -----------------------------------
    CategoriesManageView getManageCategoriesView();
    CategoryDetailView getCategoryDetailView();

    // -----------------------------------
    // Kategoriegruppen
    // -----------------------------------
    CategoryGroupManageView getCategoryGroupManageView();
    CategoryGroupDetailView getCategoryGroupDetailView();

    // -----------------------------------
    // Regelmäßige Umsätze
    // -----------------------------------
    FixedTurnoverManageView getFixedTurnoverManageView();
    FixedTurnoverDetailView getFixedTurnoverDetailView();

    // -----------------------------------
    // Einzigartige Ausgaben
    // -----------------------------------
    UniqueTurnoverManageView getUniqueExpensesManageView();
    UniqueTurnoverDetailView getUniqueExpenseDetailView();

    // -----------------------------------
    // Abrechnungen
    // -----------------------------------
    BillingManageView getBillingManageView();
    BillingDetailView getBillingDetailView();

    // -----------------------------------
    // Umsätze importieren
    // -----------------------------------
    ImportView getImportView();
    FixedTurnoverWizardView getFixedTurnoverWizardView();

    // -----------------------------------
    // Technische Ansichten & Services
    // -----------------------------------
    MigrationService getMigrationService();
    MigrationView getMigrationView();

    PropertiesServiceImpl getPropertiesService();

    // -----------------------------------
    // Sonstiges
    // -----------------------------------
    Converters getConverters();
    BbUncaughtExceptionHandler getUncaughtExceptionHandler();
}
