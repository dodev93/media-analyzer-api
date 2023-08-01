package com.dodev.analyzer.api.v1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Forecast {

    private Double used;
    private Integer days;
    private Double dailyUsage;
    private Double monthlyUsage;
    private Double actualMonthOverused;
    private Double actualMonthOverpaid;

}
