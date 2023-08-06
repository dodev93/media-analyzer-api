package com.dodev.analyzer.domain;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class MediaAnalyzerServiceImpl implements MediaAnalyzerService {

    @Override
    public MediaDetails getMediaDetails(Long mediaId) {
        MediaDetails mediaDetails = new MediaDetails();

        mediaDetails.setId(mediaId);
        if (mediaId == 1) {
            mediaDetails.setName("Cold water");
            mediaDetails.setPaymentType(MediaDetails.PaymentType.PREPAID);
            mediaDetails.setUnit("cm3");
        } else if (mediaId == 2) {
            mediaDetails.setName("Hot water");
            mediaDetails.setPaymentType(MediaDetails.PaymentType.PREPAID);
            mediaDetails.setUnit("cm3");
        } else if (mediaId == 3) {
            mediaDetails.setName("Heating");
            mediaDetails.setPaymentType(MediaDetails.PaymentType.PREPAID);
            mediaDetails.setUnit("KJ");
            mediaDetails.setTransformation(v -> v * 0.0036); // convert kWH to KJ
        } else if (mediaId == 4) {
            mediaDetails.setName("Electricity");
            mediaDetails.setPaymentType(MediaDetails.PaymentType.USAGE);
            mediaDetails.setUnit("KWh");
        }

        return mediaDetails;
    }


    @Override
    public List<MediaState> getMediaStates(Long mediaId, LocalDate from, LocalDate to) {
        return getStates(mediaId, from, to);
    }

    @Override
    public MediaState getLastMediaState(Long mediaId) {
        List<MediaState> states = getStates(mediaId, null, null);
        return states.get(states.size() - 1);
    }

    @Override
    public BillingPeriod getBillingPeriod(LocalDate date) {
        return getBillingPeriodList(date, date).stream()
                                               .filter(getBillingPeriodPredicate(date))
                                               .findFirst()
                                               .get();
    }

    @Override
    public List<BillingPeriod> getBillingPeriods(LocalDate from, LocalDate to) {
        return getBillingPeriodList(from, to);
    }

    private List<BillingPeriod> getBillingPeriodList(LocalDate from, LocalDate to) {
        List<BillingPeriod> billingPeriods = new ArrayList<>();

        BillingPeriod billingPeriod2022 = new BillingPeriod();
        billingPeriod2022.setId(1l);
        billingPeriod2022.setFrom(LocalDate.of(2022,8,1));
        billingPeriod2022.setTo(LocalDate.of(2022,12,31));

        List<MediaState> mediaInitialStates = new ArrayList<>();
        mediaInitialStates.add(new MediaState(1l, LocalDate.of(2022,8,31), 396.69 + 54.26));
        mediaInitialStates.add(new MediaState(2l, LocalDate.of(2022,8,31), 183.4 + 64.73));
        mediaInitialStates.add(new MediaState(3l, LocalDate.of(2022,8,31), 54689.3));
        mediaInitialStates.add(new MediaState(4l, LocalDate.of(2022,8,31), 7.0));
        billingPeriod2022.setMediaInitialStates(mediaInitialStates);

        List<MediaMonthlyPrepaid> prepaids = new ArrayList<>();
        prepaids.add(new MediaMonthlyPrepaid(1l, 3.0, BigDecimal.valueOf(13.67)));
        prepaids.add(new MediaMonthlyPrepaid(2l, 2.0, BigDecimal.valueOf(13.67 + 18.65)));
        prepaids.add(new MediaMonthlyPrepaid(3l, 2.35, BigDecimal.valueOf(105.21)));
        prepaids.add(new MediaMonthlyPrepaid(4l, 0.0, BigDecimal.valueOf(0.0)));
        billingPeriod2022.setPrepaids(prepaids);

        billingPeriods.add(billingPeriod2022);


        BillingPeriod billingPeriod2023 = new BillingPeriod();
        billingPeriod2023.setId(2l);
        billingPeriod2023.setFrom(LocalDate.of(2023,1,1));
        billingPeriod2023.setTo(LocalDate.of(2023,12,31));

        List<MediaState> mediaInitialStates2023 = new ArrayList<>();
        MediaDetails media = getMediaDetails(1l);
        mediaInitialStates2023.add(new MediaState(media.getId(), LocalDate.of(2023,1,1), media.transform(412.04 + 56.65)));
        media = getMediaDetails(2l);
        mediaInitialStates2023.add(new MediaState(media.getId(), LocalDate.of(2023,1,1), media.transform(189.82 + 65.24)));
        media = getMediaDetails(3l);
        mediaInitialStates2023.add(new MediaState(media.getId(), LocalDate.of(2023,1,1), media.transform(55027.6)));
        media = getMediaDetails(4l);
        mediaInitialStates2023.add(new MediaState(media.getId(), LocalDate.of(2023,1,1), media.transform(529.0)));
        billingPeriod2023.setMediaInitialStates(mediaInitialStates2023);

        List<MediaMonthlyPrepaid> prepaids2023 = new ArrayList<>();
        prepaids2023.add(new MediaMonthlyPrepaid(1l, 3.0, BigDecimal.valueOf(13.67)));
        prepaids2023.add(new MediaMonthlyPrepaid(2l, 2.0, BigDecimal.valueOf(13.67 + 18.65)));
        prepaids2023.add(new MediaMonthlyPrepaid(3l, 1.0, BigDecimal.valueOf(105.21)));
        prepaids2023.add(new MediaMonthlyPrepaid(4l, 0.0, BigDecimal.valueOf(0.0)));
        billingPeriod2023.setPrepaids(prepaids);

        billingPeriods.add(billingPeriod2023);

        return billingPeriods;
    }

    private List<MediaState> getStates(Long mediaId, LocalDate from, LocalDate to) {
        List<MediaState> states = new ArrayList<>();

        MediaDetails media = getMediaDetails(mediaId);

        if (mediaId == 1) {
            states.add(new MediaState(mediaId, LocalDate.of(2022,8,31), 396.69 + 54.26));
            states.add(new MediaState(mediaId, LocalDate.of(2022,11,19), 406.56 + 54.26));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,2), 407.84 + 55.92));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,10), 408.54 + 56.06));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,16), 409.01 + 56.12));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,29), 410.36 + 56.39));
            states.add(new MediaState(mediaId, LocalDate.of(2023,1,13), 412.04 + 56.65));
            states.add(new MediaState(mediaId, LocalDate.of(2023,7,5), 428.83 + 59.51));
        } else if (mediaId == 2) {
            states.add(new MediaState(mediaId, LocalDate.of(2022,8,31), 183.4 + 64.73));
            states.add(new MediaState(mediaId, LocalDate.of(2022,11,19), 187.17 + 64.73));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,2), 187.67 + 65));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,10), 187.95 + 65.07));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,16), 188.2 + 65.08));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,29), 188.92 + 65.16));
            states.add(new MediaState(mediaId, LocalDate.of(2023,1,13), 189.82 + 65.24));
            states.add(new MediaState(mediaId, LocalDate.of(2023,7,5), 198.15 + 65.73));
        } else if (mediaId == 3) {
            states.add(new MediaState(mediaId, LocalDate.of(2022,8,31), media.transform(54661.4)));
            states.add(new MediaState(mediaId, LocalDate.of(2022,11,24), media.transform(54689.3)));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,2), media.transform(54743.6)));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,10), media.transform(54787.7)));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,16), media.transform(54861.0)));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,23), media.transform(54973.0)));
            states.add(new MediaState(mediaId, LocalDate.of(2023,1,1), media.transform(55027.6)));
            states.add(new MediaState(mediaId, LocalDate.of(2023,1,13), media.transform(55084.0)));
            states.add(new MediaState(mediaId, LocalDate.of(2023,1,31), media.transform( 55303.9)));
        } else if (mediaId == 4) {
            states.add(new MediaState(mediaId, LocalDate.of(2022,8,31), 7.0));
            states.add(new MediaState(mediaId, LocalDate.of(2022,12,11), 402.0));
            states.add(new MediaState(mediaId, LocalDate.of(2023,1,13), 529.0));
            states.add(new MediaState(mediaId, LocalDate.of(2023,4,5), 832.0));
            states.add(new MediaState(mediaId, LocalDate.of(2023,6,7), 1036.0));
            states.add(new MediaState(mediaId, LocalDate.of(2023,8,4), 1229.0));
        }

        return states;
    }

    private Predicate<BillingPeriod> getBillingPeriodPredicate(LocalDate date) {
        return bp -> (date.isAfter(bp.getFrom()) && date.isBefore(bp.getTo())) ||
                date.isEqual(bp.getFrom()) || date.isEqual(bp.getTo());
    }

}
