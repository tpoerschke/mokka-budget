package de.timkodiert.mokka.domain.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public record CriteriaContext<T>(CriteriaQuery<T> query, CriteriaBuilder criteriaBuilder, Root<T> root) {
}
