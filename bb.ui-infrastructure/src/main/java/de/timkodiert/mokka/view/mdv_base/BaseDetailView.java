package de.timkodiert.mokka.view.mdv_base;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Control;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.validation.ValidationWrapper;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.View;

public abstract class BaseDetailView<B> implements View {

    protected final ValidationWrapper<B> validationWrapper;
    protected final Map<String, Control> validationMap = new HashMap<>();

    @Getter
    protected BeanAdapter<B> beanAdapter = new BeanAdapter<>();

    protected BaseDetailView(ValidationWrapperFactory<B> validationWrapperFactory) {
        this.validationWrapper = validationWrapperFactory.create(validationMap, beanAdapter);
    }

    public @Nullable B getBean() {
        return beanAdapter.getBean();
    }

    public void setBean(B bean) {
        beanAdapter.setBean(bean);
        beanSet();
    }

    protected void beanSet() {
        // Kann als Callback verwendet werden
    }

    protected abstract B createEmptyEntity();

    protected boolean validate() {
        return validationWrapper.validate();
    }
}
