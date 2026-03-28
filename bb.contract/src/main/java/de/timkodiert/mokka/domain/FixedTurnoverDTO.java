package de.timkodiert.mokka.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import de.timkodiert.mokka.util.IntervalUtils;

@Getter
@Setter
public class FixedTurnoverDTO {

    private int id;
    @NotBlank(message = "{fixedTurnover.position.notBlank}")
    private String position;
    private String note;
    private Reference<CategoryDTO> category;
    private PaymentType paymentType;
    @NotNull(message = "{attribute.notNull}")
    private TurnoverDirection direction;
    private boolean usePaymentInfoForFutureOnly;
    private List<PaymentInformationDTO> paymentInformations = new ArrayList<>();
    private List<ImportRuleDTO> importRules = new ArrayList<>();
    private List<AccountTurnoverDTO> accountTurnover = new ArrayList<>();

    public static FixedTurnoverDTO create(String position, TurnoverDirection direction, ImportRuleDTO importRule) {
        FixedTurnoverDTO turnover = new FixedTurnoverDTO();
        turnover.setPosition(position);
        turnover.setDirection(direction);
        turnover.getImportRules().add(importRule);
        return turnover;
    }

    public void setPaymentInformations(List<PaymentInformationDTO> paymentInformations) {
        this.paymentInformations.clear();
        this.paymentInformations.addAll(paymentInformations);
    }

    public boolean hasOverlappingPaymentInformations() {
        return paymentInformations.stream().noneMatch(this::anyPaymentInformationInMonthYearList);
    }

    private boolean anyPaymentInformationInMonthYearList(PaymentInformationDTO payInfo) {
        return paymentInformations.stream()
                                  .filter(info -> info != payInfo)
                                  .anyMatch(info -> IntervalUtils.overlap(payInfo.getStart(), payInfo.getEnd(), info.getStart(), info.getEnd()));
    }
}
