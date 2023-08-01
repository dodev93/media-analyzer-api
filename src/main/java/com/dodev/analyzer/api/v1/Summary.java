package com.dodev.analyzer.api.v1;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Summary {

    private LocalDate from;
    private LocalDate to;
    private LocalDate lastStateDate;
    private String name;
    private Double used;
    private Double paidLastState;
    private Double paidToday;
    private Double overused;
    private Double overpaid;

}
