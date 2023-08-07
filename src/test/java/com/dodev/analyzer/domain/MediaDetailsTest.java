package com.dodev.analyzer.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaDetailsTest {

    @Test
    void transformedValue() {
        MediaDetails mediaDetails = new MediaDetails();
        mediaDetails.setTransformation(v -> v * 2.5);

        Assertions.assertEquals(7.5, mediaDetails.transform(3.0));
    }

    @Test
    void notTransformedValue() {
        MediaDetails mediaDetails = new MediaDetails();

        Assertions.assertEquals(3.0, mediaDetails.transform(3.0));
    }

}