package com.flight_booking.builder;

import com.flight_booking.domain.User;
import com.flight_booking.domain.enums.Role;
import com.flight_booking.model.request.user.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserBuilderTest {

    @InjectMocks
    private UserBuilder userBuilder;

    @Test
    void it_should_build_user() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .username("testUser")
                .password("securePassword123")
                .role(Role.PASSENGER)
                .build();

        // when
        User result = userBuilder.build(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
        assertThat(result.getPassword()).isEqualTo("securePassword123");
        assertThat(result.getRole()).isEqualTo(Role.PASSENGER);
    }

    @Test
    void it_should_build_user_with_admin_role() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .username("adminUser")
                .password("adminPass123")
                .role(Role.ADMIN)
                .build();

        // when
        User result = userBuilder.build(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("adminUser");
        assertThat(result.getPassword()).isEqualTo("adminPass123");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }
}
