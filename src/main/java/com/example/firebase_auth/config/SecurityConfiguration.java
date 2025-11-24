package com.example.firebase_auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

        private final AuthenticationProvider authenticationProvider;
        private final CustomCorsConfiguration customCorsConfiguration; 


        @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(customCorsConfiguration))
        .authorizeHttpRequests(request -> request
            .requestMatchers(
                "/api/v1/auth-service/**",
                "/api/v1/firebase/**", // Keep these public for login/register
                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**",
                "/instances")
            .permitAll()
            .anyRequest().authenticated())
        .sessionManagement(manager -> manager
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider);
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
}