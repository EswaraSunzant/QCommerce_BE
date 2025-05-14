package com.qcommerce.security;

import jakarta.servlet.http.HttpServletResponse;
// Remove this import if no other fields are autowired in this class
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // Keep this import
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Keep this import
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService; // Keep this import
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Keep this import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Keep this import
import org.springframework.security.crypto.password.PasswordEncoder; // Keep this import
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.qcommerce.user.UserRepository; // Make sure you have this import and UserRepository is public
import com.qcommerce.user.User; // Import your User entity here


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // >>>>>> REMOVE THIS FIELD <<<<<<<
    // private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // >>>>>> REMOVE THIS CONSTRUCTOR <<<<<<<
    // public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    //     this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    // }

    @Bean
    // >>>>>> INJECT JwtAuthenticationFilter AS A METHOD PARAMETER HERE <<<<<<<
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .requestCache(requestCache -> requestCache.disable()) // ✅ prevent saving requests
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login", "/test").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // >>>>>> Use the method parameter here <<<<<<<
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // >>>>>> CORRECTED UserDetailsService IMPLEMENTATION <<<<<<
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return identifier -> { // Use a general name for the parameter, as it could be email or phone
            // Use findByEmail if you authenticate by email
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email/identifier: " + identifier));

            // OR, if you authenticate by phone, use this instead:
            // return userRepository.findByPhone(identifier)
            //         .orElseThrow(() -> new UsernameNotFoundException("User not found with phone/identifier: " + identifier));
        };
    }

    @Bean
    // Add this bean to expose the AuthenticationManager
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
