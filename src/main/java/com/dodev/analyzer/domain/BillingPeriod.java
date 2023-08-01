package com.dodev.analyzer.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BillingPeriod {

    private Long id;
    private LocalDate from;
    private LocalDate to;

    private List<MediaMonthlyPrepaid> prepaids;
    private List<MediaState> mediaInitialStates;

}
