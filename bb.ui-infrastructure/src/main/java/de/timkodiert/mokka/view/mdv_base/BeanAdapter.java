package de.timkodiert.mokka.view.mdv_base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import lombok.Getter;

import de.timkodiert.mokka.exception.TechnicalException;
import de.timkodiert.mokka.util.ReflectionUtils;

public class BeanAdapter<B> {

    private final ReadOnlyBooleanWrapper isEmpty = new ReadOnlyBooleanWrapper(true);

    private final Map<String, BeanPropertyContainer<B, ?>> propertyMap = new HashMap<>();
    private final Map<String, BeanListPropertyContainer<B, ?>> listPropertyMap = new HashMap<>();

    private boolean settingNewBean = false;

    @Getter
    private B bean;

    private final ReadOnlyBooleanWrapper dirtyProperty = new ReadOnlyBooleanWrapper(false);

    public void setBean(B bean) {
        settingNewBean = true;
        this.bean = bean;
        isEmpty.setValue(bean == null);

        if (bean != null) {
            propertyMap.values().forEach(propContainer -> propContainer.setValue(bean));
            listPropertyMap.values().forEach(propContainer -> propContainer.setValue(bean));
        }
        dirtyProperty.setValue(false);
        settingNewBean = false;
    }

    public boolean isDirty() {
        return dirtyProperty.getValue();
    }

    public void setDirty(boolean dirty) {
        dirtyProperty.setValue(dirty);
    }

    public ReadOnlyBooleanProperty dirty() {
        return dirtyProperty.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty isEmpty() {
        return isEmpty.getReadOnlyProperty();
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectProperty<T> getProperty(ReflectionUtils.SerializableFunction<B, T> getter, BiConsumer<B, T> setter) {
        String getterName;
        try {
            getterName = ReflectionUtils.resolveMethodName(getter);
        } catch (ReflectiveOperationException e) {
            throw TechnicalException.forProgrammingError(e);
        }
        return (ObjectProperty<T>) propertyMap.computeIfAbsent(getterName, k -> createProperty(getter, setter)).property();
    }

    @SuppressWarnings("unchecked")
    public <T> ListProperty<T> getListProperty(ReflectionUtils.SerializableFunction<B, List<T>> getter, BiConsumer<B, List<T>> setter) {
        String getterName;
        try {
            getterName = ReflectionUtils.resolveMethodName(getter);
        } catch (ReflectiveOperationException e) {
            throw TechnicalException.forProgrammingError(e);
        }
        return (ListProperty<T>) listPropertyMap.computeIfAbsent(getterName, k -> createListProperty(getter, setter)).property();
    }

    private <T> BeanPropertyContainer<B, T> createProperty(Function<B, T> getter, BiConsumer<B, T> setter) {
        ObjectProperty<T> property = new SimpleObjectProperty<>();
        property.addListener((obs, oldVal, newVal) -> {
            if (settingNewBean) {
                return;
            }
            if (!Objects.equals(oldVal, newVal)) {
                dirtyProperty.setValue(true);
            }
            setter.accept(bean, newVal);
        });
        BeanPropertyContainer<B, T> propContainer = new BeanPropertyContainer<>(property, getter, setter);
        if (bean != null) {
            propContainer.setValue(bean);
        }
        return propContainer;
    }

    private <T> BeanListPropertyContainer<B, T> createListProperty(Function<B, List<T>> getter, BiConsumer<B, List<T>> setter) {
        ListProperty<T> property = new SimpleListProperty<>(FXCollections.observableArrayList());
        property.addListener((obs, oldVal, newVal) -> {
            if (settingNewBean) {
                return;
            }
            if (!Objects.equals(oldVal, newVal)) {
                dirtyProperty.setValue(true);
            }
            setter.accept(bean, new ArrayList<>(newVal));
        });
        BeanListPropertyContainer<B, T> propContainer = new BeanListPropertyContainer<>(property, getter, setter);
        if (bean != null) {
            propContainer.setValue(bean);
        }
        return propContainer;
    }

    record BeanPropertyContainer<D, T>(Property<T> property, Function<D, T> getter, BiConsumer<D, T> setter) {
         void setValue(D bean) {
            property.setValue(getter.apply(bean));
        }
    }

    record BeanListPropertyContainer<D, T>(ListProperty<T> property, Function<D, List<T>> getter, BiConsumer<D, List<T>> setter) {
        void setValue(D bean) {
            property.setAll(getter.apply(bean));
        }
    }
}
