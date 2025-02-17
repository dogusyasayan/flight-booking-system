package com.flight_booking.config;

import com.flight_booking.service.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless Authentication
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                        // **Genel Erişim**
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                        // **Passenger Yetkileri (SADECE GÖRME)**
                        .requestMatchers(new AntPathRequestMatcher("/flight/all")).hasAnyRole("PASSENGER", "ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/flight/{flightCode}")).hasAnyRole("PASSENGER", "ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/payment/{flightCode}/")).hasRole("PASSENGER")
                        .requestMatchers(new AntPathRequestMatcher("/seat/{flightCode}")).hasAnyRole("PASSENGER", "ADMIN") // Koltukları görmek için

                        // **Admin Yetkileri (DEĞİŞTİRME)**
                        .requestMatchers(new AntPathRequestMatcher("/flight")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/flight/{flightCode}")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/seat/{flightCode}")).hasRole("ADMIN") // Passenger artık POST yapamaz
                        .requestMatchers(new AntPathRequestMatcher("/seat/update")).hasRole("ADMIN") // Güncelleme

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
