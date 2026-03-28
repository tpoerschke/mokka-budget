package de.timkodiert.mokka.domain;

import java.io.Serializable;

public record Reference<T>(Class<T> refClass, int id, String name) implements Serializable {
}
