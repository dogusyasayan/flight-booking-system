package com.flight_booking.domain.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"),
    PASSENGER("passenger");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}
