package de.timkodiert.mokka.injector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.inject.Inject;

import de.timkodiert.mokka.db.MigrationView;
import de.timkodiert.mokka.view.AnnualOverviewView;
import de.timkodiert.mokka.view.MonthlyOverviewView;
import de.timkodiert.mokka.view.View;
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

public class ControllerFactory {

    private final ViewComponent viewComponent;

    private final Map<Class<?>, Supplier<View>> viewControllerMap = new HashMap<>();

    @Inject
    public ControllerFactory(ViewComponent viewComponent) {
        this.viewComponent = viewComponent;
        registerController();
    }

    private void registerController() {
        // Daten-/Steuerungsansichten
        viewControllerMap.put(AnnualOverviewView.class, viewComponent::getAnnualOverviewView);
        viewControllerMap.put(MonthlyOverviewView.class, viewComponent::getMonthlyOverview);
        viewControllerMap.put(BudgetWidget.class, viewComponent::getBudgetWidget);
        viewControllerMap.put(ImportView.class, viewComponent::getImportView);
        viewControllerMap.put(FixedTurnoverWizardView.class, viewComponent::getFixedTurnoverWizardView);
        viewControllerMap.put(AnalysisView.class, viewComponent::getAnalysisView);
        // MDV / Stammdaten / Bewegungsdaten
        viewControllerMap.put(FixedTurnoverManageView.class, viewComponent::getFixedTurnoverManageView);
        viewControllerMap.put(FixedTurnoverDetailView.class, viewComponent::getFixedTurnoverDetailView);
        viewControllerMap.put(UniqueTurnoverManageView.class, viewComponent::getUniqueExpensesManageView);
        viewControllerMap.put(UniqueTurnoverDetailView.class, viewComponent::getUniqueExpenseDetailView);
        viewControllerMap.put(CategoriesManageView.class, viewComponent::getManageCategoriesView);
        viewControllerMap.put(CategoryDetailView.class, viewComponent::getCategoryDetailView);
        viewControllerMap.put(CategoryGroupManageView.class, viewComponent::getCategoryGroupManageView);
        viewControllerMap.put(CategoryGroupDetailView.class, viewComponent::getCategoryGroupDetailView);
        viewControllerMap.put(BillingManageView.class, viewComponent::getBillingManageView);
        viewControllerMap.put(BillingDetailView.class, viewComponent::getBillingDetailView);
        // Technische Ansichten
        viewControllerMap.put(MigrationView.class, viewComponent::getMigrationView);
        // Sonstige Ansichten
        viewControllerMap.put(AboutView.class, viewComponent::getAboutView);
    }

    public View create(Class<?> viewControllerClass) {
        return viewControllerMap.get(viewControllerClass).get();
    }
}
