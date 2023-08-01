package com.dodev.analyzer.api.v1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaStatistics {

    private Summary summary;
    private Forecast forecast;

}
