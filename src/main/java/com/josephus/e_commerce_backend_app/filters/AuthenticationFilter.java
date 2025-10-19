package com.josephus.com.ecommercebackend.filters;
//
import com.josephus.com.ecommercebackend.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import static org.springframework.security.config.Customizer.withDefaults;
@Component
@EnableWebSecurity
public class AuthenticationFilter {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http.authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/users/register/**").permitAll()
//                        .requestMatchers("/api/users/login/**").permitAll()
//                        .requestMatchers("/api/users/logout/**").permitAll()
//                        .requestMatchers("/api/users/confirm/**").permitAll()
//                        .requestMatchers("/api/users/contact/**").permitAll()
//                        .requestMatchers("/api/users/**").permitAll()
//                        .requestMatchers("/api/customer/order-items/**").permitAll()
//                        .requestMatchers("/api/customer/order-items/order/**").permitAll()
//                        .requestMatchers("/api/admin/categories/**").permitAll()
//                        .requestMatchers("/api/customer/categories/**").permitAll()
//                        .requestMatchers("/api/admin/products/**").permitAll()
//                        .requestMatchers("/api/customer/products/**").permitAll()
//                        .requestMatchers("/api/customer/products/category/**").permitAll()
//                        .requestMatchers("/api/admin/orders/**").permitAll()
//                        .requestMatchers("/api/customer/orders/**").permitAll()
//                        .requestMatchers("/api/admin/payments/**").permitAll()
//                        .requestMatchers("/api/customer/payments/**").permitAll()
//                        .anyRequest().authenticated())
//                .httpBasic(withDefaults())
//                .formLogin(withDefaults())
//                //.csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrf -> csrf .ignoringRequestMatchers(
//                        new AntPathRequestMatcher("/api/users/register/**"),
//                        new AntPathRequestMatcher("/api/users/login/**"),
//                        new AntPathRequestMatcher("/api/users/logout/**"),
//                        new AntPathRequestMatcher("/api/users/confirm/**"),
//                        new AntPathRequestMatcher("/api/users/contact/**"),
//                        new AntPathRequestMatcher("/api/users/**"),
//                        new AntPathRequestMatcher("/api/customer/order-items/**"),
//                        new AntPathRequestMatcher("/api/customer/order-items/order/**"),
//                        new AntPathRequestMatcher("/api/admin/categories/**"),
//                        new AntPathRequestMatcher("/api/customer/categories/**"),
//                        new AntPathRequestMatcher("/api/admin/products/**"),
//                        new AntPathRequestMatcher("/api/customer/products/**"),
//                        new AntPathRequestMatcher("/api/customer/products/category/**"),
//                        new AntPathRequestMatcher("/api/admin/orders/**"),
//                        new AntPathRequestMatcher("/api/customer/orders/**"),
//                        new AntPathRequestMatcher("/api/admin/payments/**"),
//                        new AntPathRequestMatcher("/api/customer/payments/**")
//                ));
//        return http.build();
//    }

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        logSecurityContext();

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/register/**",
                                "/api/users/login/**",
                                "/api/users/logout/**",
                                "/api/users/confirm/**",
                                "/api/users/forgot-password/**",
                                "/api/users/reset-password/**",
                                "/api/users/contact/**",
                                "/api/customer/products/**",
                                "/api/customer/categories/**",
                                "/access-denied", // Allow access to custom error pages
                                "/authentication-failed",
                                "/not-found",
                                "/api/users/**",
                                "/api/admin/categories/**",
                                "/api/admin/products/**",
                                "/api/admin/payments/**",
                                "/api/admin/orders/**",
                                "/api/customer/orders/**",
                                "/api/customer/order-items/**",
                                "/api/customer/order-items/order/**",
                                "/api/customer/products/category/**",
                                "/api/customer/payments/**"

                        ).permitAll()
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/api/customer/**"
                        ).hasRole("CUSTOMER")
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/api/users/register/**"),
                        new AntPathRequestMatcher("/api/users/login/**"),
                        new AntPathRequestMatcher("/api/users/logout/**"),
                        new AntPathRequestMatcher("/api/users/confirm/**"),
                        new AntPathRequestMatcher("/api/users/forgot-password/**"),
                        new AntPathRequestMatcher("/api/users/reset-password/**"),
                        new AntPathRequestMatcher("/api/users/contact/**"),
                        new AntPathRequestMatcher("/api/customer/products/**"),
                        new AntPathRequestMatcher("/api/customer/categories/**"),
                        new AntPathRequestMatcher("/api/users/contact/**"),
                        new AntPathRequestMatcher("/api/customer/order-items/**"),
                        new AntPathRequestMatcher("/api/customer/order-items/order/**"),
                        new AntPathRequestMatcher("/api/admin/categories/**"),
                        new AntPathRequestMatcher("/api/admin/products/**"),
                        new AntPathRequestMatcher("/api/customer/products/category/**"),
                        new AntPathRequestMatcher("/api/admin/orders/**"),
                        new AntPathRequestMatcher("/api/customer/orders/**"),
                        new AntPathRequestMatcher("/api/admin/payments/**"),
                        new AntPathRequestMatcher("/api/customer/payments/**")
                ))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            //logger.error("Access Denied: " + accessDeniedException.getMessage());
                            response.sendRedirect(frontendBaseUrl+"/access-denied");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            //logger.error("Authentication Failed: " + authException.getMessage());
                            response.sendRedirect(frontendBaseUrl+"/authentication-failed");
                        })
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> {
                                    response.sendRedirect(frontendBaseUrl + "/not-found");
                                },
                                new AntPathRequestMatcher("/**")
                        )
                );

        return http.build();
    }

    private void logSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logger.info("User authenticated: " + authentication.getName() + ", Authorities: " + authentication.getAuthorities());
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                logger.info("User Role: " + authority.getAuthority());
            }
        } else {
            logger.info("No authentication in security context");
        }
    }
}

