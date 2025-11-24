package com.example.firebase_auth.filter;


import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.firebase_auth.config.SecurityConstants;
import com.example.firebase_auth.repo.UserReposiitory;
import com.example.firebase_auth.service.FirebaseAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final FirebaseAuth firebaseAuth;
    private final UserReposiitory userRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws IOException {
        String authorizationHeader = request.getHeader(SecurityConstants.ACCESS_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.replace(BEARER_PREFIX, "");
            
            try {
                FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, true);
                String email = firebaseToken.getEmail();
                
                UserDetails userDetails = firebaseAuthService.loadUserByUsername(email);
                
                var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);   
                
            } catch (FirebaseAuthException | org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                setAuthErrorDetails(response, "Authentication failure: Invalid Firebase token");
                return;
            }
        }
        
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new IOException("Filter chain error", e);
        }
    }

    private void setAuthErrorDetails(HttpServletResponse response, String detail) throws IOException {
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        response.setStatus(unauthorized.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(unauthorized, detail);
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}