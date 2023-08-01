package com.dodev.analyzer.domain;

import java.time.LocalDate;

public class MediaPaidValueCalculator {

    private BillingPeriodProvider billingPeriodProvider;

    public MediaPaidValueCalculator(BillingPeriodProvider billingPeriodProvider) {
        this.billingPeriodProvider = billingPeriodProvider;
    }

    public Double ofDay(LocalDate date) {
        return calculateMediaPaid(date);
    }

    private Double calculateMediaPaid(LocalDate date) {

        BillingPeriod billingPeriod = billingPeriodProvider.getForDay(date);

        MediaState initialMediaState = billingPeriod.getMediaInitialStates().get(0);
        MediaMonthlyPrepaid mediaMonthlyPrepaid = billingPeriod.getPrepaids().get(0);

        int startYear = billingPeriod.getFrom().getYear();
        int endYear = date.getYear();

        Double mediaPaid = initialMediaState.getValue();

        for (int year = startYear; year <= endYear; year++) {

            int startMonth = year == startYear ? billingPeriod.getFrom().getMonthValue() : 1;
            int endMonth = year == endYear ? date.getMonthValue() : 12;

            int startDay = billingPeriod.getFrom().getDayOfMonth();
            int day = date.getDayOfMonth();

            for (int month = startMonth; month <= endMonth; month++) {

                if (billingPeriod.getFrom().isEqual(date)) {
                    mediaPaid = initialMediaState.getValue();
                } else if (year == endYear && month == endMonth) {
                    long dayOfMonth = date.getDayOfMonth();
                    int lengthOfMonth = date.lengthOfMonth();
                    double paidValue = (mediaMonthlyPrepaid.getValue() / lengthOfMonth) * dayOfMonth;
                    mediaPaid += paidValue;
                } else {
                    mediaPaid += mediaMonthlyPrepaid.getValue();
                }
            }
        }

        return mediaPaid;
    }

}
