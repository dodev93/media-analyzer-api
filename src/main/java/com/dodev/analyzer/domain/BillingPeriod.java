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

    public MediaMonthlyPrepaid selectMediaPrepaid(Long mediaId) {
        MediaMonthlyPrepaid mediaMonthlyPrepaid = null;
        for (MediaMonthlyPrepaid prepaid : this.prepaids) {
            if (prepaid.getMediaId().equals(mediaId)) {
                mediaMonthlyPrepaid = prepaid;
                break;
            }
        }
        return mediaMonthlyPrepaid;
    }

    public MediaState selectInitialStatePrepaid(Long mediaId) {
        MediaState initialState = null;
        for (MediaState state : this.mediaInitialStates) {
            if (state.getMediaId().equals(mediaId)) {
                initialState = state;
                break;
            }
        }
        return initialState;
    }
}
