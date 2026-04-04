package de.timkodiert.mokka.domain;

import java.util.List;

public record ManageViewContainer<T>(List<T> items, int numberOfPages) {
}
