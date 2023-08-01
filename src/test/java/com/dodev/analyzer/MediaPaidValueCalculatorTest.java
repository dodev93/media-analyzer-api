package com.dodev.analyzer;

import com.dodev.analyzer.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class MediaPaidValueCalculatorTest {

    @Test
    void singlePeriodTest() {

        MediaPaidValueCalculator calculator = new MediaPaidValueCalculator(new TestBillingProvider());

        Double usageSummary = calculator.ofDay(LocalDate.of(2023,7,15));

        double days = 1.0 / 31 * 15;
        Assertions.assertEquals(6 + 30 + days, usageSummary);
    }

    @Test
    void firstDayPeriodTest() {

        MediaPaidValueCalculator calculator = new MediaPaidValueCalculator(new TestBillingProvider());

        Double usageSummary = calculator.ofDay(LocalDate.of(2021,1,1));

        Assertions.assertEquals(6.0, usageSummary);
    }

    @Test
    void firstPeriodTest() {

        MediaPaidValueCalculator calculator = new MediaPaidValueCalculator(new TestBillingProvider());

        Double usageSummary = calculator.ofDay(LocalDate.of(2021,1,10));

        double days = 1.0 / 31 * 10;
        Assertions.assertEquals(6.0 + days, usageSummary);
    }

    class TestBillingProvider implements BillingPeriodProvider {
        @Override
        public BillingPeriod getForDay(LocalDate date) {
            BillingPeriod billingPeriod = new BillingPeriod();
            billingPeriod.setFrom(LocalDate.of(2021,1,1));

            List<MediaState> initialStates = new ArrayList<>();
            MediaState state = new MediaState();
            state.setValue(6.0);
            initialStates.add(state);
            billingPeriod.setMediaInitialStates(initialStates);

            List<MediaMonthlyPrepaid> prepaids = new ArrayList<>();
            MediaMonthlyPrepaid prepaid = new MediaMonthlyPrepaid();
            prepaid.setValue(1.0);
            prepaid.setUnitPrice(BigDecimal.valueOf(16.50));
            prepaids.add(prepaid);
            billingPeriod.setPrepaids(prepaids);
            return billingPeriod;
        }
    }


}