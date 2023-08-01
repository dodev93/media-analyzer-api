package com.dodev.analyzer.domain;

import com.dodev.analyzer.api.v1.Forecast;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ForecastProviderTest {

    @Test
    public void forecast() {
        ForecastProvider forecastProvider = new ForecastProvider();

        List<MediaState> states = new ArrayList<>();
        states.add(new MediaState(1l, LocalDate.of(2023,6,1), 1.0));
        states.add(new MediaState(1l, LocalDate.of(2023,7,1), 2.0));
        states.add(new MediaState(1l, LocalDate.of(2023,8,1), 5.0));

        Double prepaid = 2.0;
        Double unitPrice = 10.0;

        Forecast forecast = forecastProvider.prepareForecast(states, prepaid, unitPrice);

        double expectedUsed = 3.0;
        int expectedDays = 31;
        double expectedDailyUsage = expectedUsed / expectedDays;
        double expectedMonthlyUsage = expectedDailyUsage * 31;
        double expectedMonthlyOverused = expectedMonthlyUsage - prepaid;
        double expectedMonthlyOverpaid = (expectedMonthlyOverused) * unitPrice;

        Assertions.assertEquals(expectedUsed, forecast.getUsed());
        Assertions.assertEquals(expectedDays, forecast.getDays());
        Assertions.assertEquals(expectedDailyUsage, forecast.getDailyUsage());
        Assertions.assertEquals(expectedMonthlyUsage, forecast.getMonthlyUsage());
        Assertions.assertEquals(expectedMonthlyOverused, forecast.getActualMonthOverused());
        Assertions.assertEquals(expectedMonthlyOverpaid, forecast.getActualMonthOverpaid());

    }

}