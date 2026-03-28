package de.timkodiert.mokka.validation;

import java.util.Map;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import javafx.scene.control.Control;

import de.timkodiert.mokka.view.mdv_base.BeanAdapter;

@AssistedFactory
public interface ValidationWrapperFactory<T> {
    ValidationWrapper<T> create(@Assisted Map<String, Control> propertyNodeMap, @Assisted BeanAdapter<T> beanAdapter);
}
