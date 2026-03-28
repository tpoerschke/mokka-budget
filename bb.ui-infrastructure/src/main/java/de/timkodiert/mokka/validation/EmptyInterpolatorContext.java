package de.timkodiert.mokka.validation;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.metadata.ConstraintDescriptor;

public class EmptyInterpolatorContext implements MessageInterpolator.Context {

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
    }

    @Override
    public Object getValidatedValue() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
