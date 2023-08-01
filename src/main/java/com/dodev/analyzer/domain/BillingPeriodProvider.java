package com.dodev.analyzer.domain;

import java.time.LocalDate;

public interface BillingPeriodProvider {

    BillingPeriod getForDay(LocalDate date);

}
