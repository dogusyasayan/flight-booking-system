package com.flight_booking.builder;

import com.flight_booking.domain.User;
import com.flight_booking.model.request.user.SignUpRequest;
import org.springframework.stereotype.Component;

@Component
public class UserBuilder {

    public User build(SignUpRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .build();
    }
}
