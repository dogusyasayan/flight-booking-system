package com.flight_booking.service;

import com.flight_booking.builder.UserBuilder;
import com.flight_booking.domain.User;
import com.flight_booking.domain.enums.Role;
import com.flight_booking.model.request.user.SignInRequest;
import com.flight_booking.model.request.user.SignUpRequest;
import com.flight_booking.repository.UserRepository;
import com.flight_booking.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBuilder userBuilder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = SignUpRequest.builder()
                .username("testUser")
                .password("password123")
                .role(Role.PASSENGER)
                .build();

        signInRequest = SignInRequest.builder()
                .username("testUser")
                .password("password123")
                .build();

        user = User.builder()
                .username("testUser")
                .password("encodedPassword")
                .role(Role.PASSENGER)
                .build();
    }

    @Test
    void it_should_sign_up_successfully() {
        // given
        given(userRepository.findByUsername(signUpRequest.getUsername())).willReturn(Optional.empty());
        given(userBuilder.build(signUpRequest)).willReturn(user);
        given(passwordEncoder.encode(signUpRequest.getPassword())).willReturn("encodedPassword");

        // when
        authService.signUp(signUpRequest);

        // then
        verify(userRepository).save(user);
    }

    @Test
    void it_should_throw_exception_when_user_already_exists_on_sign_up() {
        // given
        given(userRepository.findByUsername(signUpRequest.getUsername())).willReturn(Optional.of(user));

        // when
        Throwable thrown = catchThrowable(() -> authService.signUp(signUpRequest));

        // then
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessage("User already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void it_should_sign_in_successfully() {
        // given
        given(userRepository.findByUsername(signInRequest.getUsername())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())).willReturn(true);
        given(jwtUtil.generateToken(user.getUsername(), user.getRole())).willReturn("mockJwtToken");

        // when
        String token = authService.signIn(signInRequest);

        // then
        assertThat(token).isEqualTo("mockJwtToken");
    }

    @Test
    void it_should_throw_exception_when_user_not_found_on_sign_in() {
        // given
        given(userRepository.findByUsername(signInRequest.getUsername())).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> authService.signIn(signInRequest));

        // then
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void it_should_throw_exception_when_password_is_incorrect_on_sign_in() {
        // given
        given(userRepository.findByUsername(signInRequest.getUsername())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())).willReturn(false);

        // when
        Throwable thrown = catchThrowable(() -> authService.signIn(signInRequest));

        // then
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid password");
    }
}
