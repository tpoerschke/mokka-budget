package de.timkodiert.mokka.view.mdv_base;

public interface DtoAdapterBase<D> {

    boolean isDirty();
    D getDto();
}
