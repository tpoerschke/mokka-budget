package de.timkodiert.mokka.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentType {
    MONTHLY, ANNUAL, SEMIANNUAL, QUARTERLY
}
