package com.nidas.recipesapp.config;

import com.nidas.recipesapp.model.Jwt;
import com.nidas.recipesapp.service.ChiefService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Service
public class JwtFilterService extends OncePerRequestFilter {

    private final ChiefService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;

    public JwtFilterService(ChiefService userService, HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService) {
        this.userService = userService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;

    }

    @Override
    protected void doFilterInternal(  HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Jwt jwt = null;
        boolean isTokenExpired = true;
        try {
            String username = null;
            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                jwt = jwtService.tokenbyvalue(token);
                isTokenExpired = jwtService.isTokenExpired(token);
                username=jwtService.extractUsername(token);
            }

            if (!isTokenExpired
                    && jwt.getChief().getEmail().equals(username)
                    && SecurityContextHolder.getContext().getAuthentication()==null) {

                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }catch (Exception exception){
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
