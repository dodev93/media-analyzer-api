package com.dodev.analyzer.domain;

import com.dodev.analyzer.api.v1.Forecast;
import com.dodev.analyzer.domain.MediaState;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ForecastProvider {

    public Forecast prepareForecast(List<MediaState> states, Double prepaid, Double unitPrice) {
        Forecast forecast = new Forecast();

        MediaState actualState = states.get(states.size() - 1);
        MediaState lastState = states.get(states.size() - 2);

        double lastUsed = actualState.getValue() - lastState.getValue();
        forecast.setUsed(lastUsed);
        long daysBetweenStates = lastState.getDate().until(actualState.getDate(), ChronoUnit.DAYS);
        forecast.setDays((int) daysBetweenStates);
        double dailyUsage = lastUsed / (double) daysBetweenStates;
        forecast.setDailyUsage(dailyUsage);
        int lengthOfMonth = LocalDate.now().lengthOfMonth();
        double monthlyUsage = dailyUsage * lengthOfMonth;
        forecast.setMonthlyUsage(monthlyUsage);
        double actualMonthOverused = monthlyUsage - prepaid;
        forecast.setActualMonthOverused(actualMonthOverused);
        forecast.setActualMonthOverpaid(actualMonthOverused * unitPrice);

        return forecast;
    }
}