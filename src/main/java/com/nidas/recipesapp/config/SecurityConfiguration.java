package com.nidas.recipesapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration {
    private final ConfigurationCryptPassword configurationCryptPassword;
    private final JwtFilterService jwtFilterService;
    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(ConfigurationCryptPassword configurationCryptPassword, JwtFilterService jwtFilterService, UserDetailsService userDetailsService) {
        this.configurationCryptPassword = configurationCryptPassword;
        this.jwtFilterService = jwtFilterService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST,"/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/activation").permitAll()
                        .requestMatchers(HttpMethod.POST,"/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.GET,"/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/modifier-password").permitAll()
                        .requestMatchers(HttpMethod.POST,"/nouveau-password").permitAll()
                        .requestMatchers(HttpMethod.POST,"/deconnexion").permitAll()
                        //.requestMatchers(HttpMethod.GET, "/chief/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .addFilterBefore(jwtFilterService, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(configurationCryptPassword.passwordEncoder());
        return authenticationProvider;
    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
//        configuration.setAllowCredentials(true);
//        configuration.applyPermitDefaultValues();
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "HEAD")); // Spécifier les méthodes autorisées
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Spécifier les en-têtes autorisés
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }

}
