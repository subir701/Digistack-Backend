package com.digistackBackend.filter;

import com.digistackBackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //check if the Authorization header is present and starts with "Bearer "
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        //Extract token
        final String jwt = authHeader.substring(7);
        //Extract username, this will also check the validity of JWT's signature
        final String email = jwtService.extractUsername(jwt);

        //Ensure user is not already authenticated
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //we can't directly ask the method to return List<String> since we can't pass List<String> as a class type due to Type erasure
            String role = jwtService.extractRole(jwt);

            // this will check both the signature validity and whether the token is expired or not
            if(!jwtService.isTokenExpired(jwt)){

                // creating a Set to ensure no duplicates being added un-necessarily in the Authentication object
                Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,null,authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // setting the Authentication object to mark the authentication as done
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        // continuing with the filter chain
        filterChain.doFilter(request,response);
    }
}
