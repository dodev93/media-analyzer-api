package com.dodev.analyzer.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;

@Setter
@Getter
public class MediaDetails {

    private Long id;
    private String name;
    private String unit;
    private PaymentType paymentType;

    private DoubleUnaryOperator transformation;

    public Double transform(Double value) {
        if (!isTransform())
            return value;
        return transformation.applyAsDouble(value);
    }

    private boolean isTransform() {
        return transformation != null;
    }

    public enum PaymentType {
        PREPAID, USAGE;
    }
}
