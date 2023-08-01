package com.dodev.analyzer.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public class BillingPeriodProviderImpl implements BillingPeriodProvider {

    private final List<BillingPeriod> billingPeriods;

    public BillingPeriodProviderImpl(List<BillingPeriod> billingPeriods) {
        this.billingPeriods = billingPeriods;
    }

    @Override
    public BillingPeriod getForDay(LocalDate date) {

        BillingPeriod billingPeriod = billingPeriods.stream()
                                                    .filter(getBillingPeriodPredicate(date))
                                                    .findFirst()
                                                    .orElseThrow(() -> new IllegalStateException());

        return billingPeriod;
    }

    private Predicate<BillingPeriod> getBillingPeriodPredicate(LocalDate date) {
        return bp -> (date.isAfter(bp.getFrom()) && date.isBefore(bp.getTo())) ||
                     date.isEqual(bp.getFrom()) || date.isEqual(bp.getTo());
    }

}
