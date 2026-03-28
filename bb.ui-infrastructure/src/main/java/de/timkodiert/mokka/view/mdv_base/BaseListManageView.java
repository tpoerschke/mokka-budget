package de.timkodiert.mokka.view.mdv_base;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.i18n.LanguageManager;

public abstract class BaseListManageView<B> extends BaseManageView<B> {

    @FXML
    protected TableView<B> entityTable;

    private final DialogFactory dialogFactory;

    protected @Nullable TableRow<B> lastSelectedRow;

    protected BaseListManageView(FXMLLoader fxmlLoader,
                                 DialogFactory dialogFactory,
                                 LanguageManager languageManager) {
        super(fxmlLoader, languageManager);
        this.dialogFactory = dialogFactory;
    }

    protected abstract B discardChanges(B beanToDiscard);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        entityTable.setRowFactory(tableView -> {
            TableRow<B> row = new TableRow<>();
            row.setOnMouseClicked(event -> handleMouseClickOnTableRow(event, row));
            return row;
        });
    }

    private void handleMouseClickOnTableRow(MouseEvent event, TableRow<B> row) {
        if (event.getClickCount() != 1 || row.isEmpty()) {
            return;
        }
        if (!detailView.isDirty()) {
            detailView.setBean(row.getItem());
            lastSelectedRow = row;
            return;
        }

        Alert alert = dialogFactory.buildConfirmationDialog();
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElseThrow().equals(DialogFactory.CANCEL)) {
            entityTable.getSelectionModel().select(detailView.getBean());
            return;
        }
        if (result.orElseThrow().equals(DialogFactory.SAVE_CHANGES) && !detailView.save()) {
            entityTable.getSelectionModel().select(detailView.getBean());
            return;
        }
        if (result.orElseThrow().equals(DialogFactory.DISCARD_CHANGES) && lastSelectedRow != null) {
            B discardedBean = discardChanges(detailView.getBean());
            entityTable.getItems().set(lastSelectedRow.getIndex(), discardedBean);
        }
        detailView.setBean(row.getItem());
        lastSelectedRow = row;
    }
}
