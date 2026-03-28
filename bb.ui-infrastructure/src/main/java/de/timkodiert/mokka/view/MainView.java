package de.timkodiert.mokka.view;

import org.jspecify.annotations.Nullable;

public interface MainView {
    @Nullable
    View loadViewPartial(FxmlResource resource);
}
