package com.dodev.analyzer.api.v1;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MediaSummaryPackage {

    private String type;
    private LocalDate from;
    private LocalDate to;
    private List<MediaSummary> summary;

}
