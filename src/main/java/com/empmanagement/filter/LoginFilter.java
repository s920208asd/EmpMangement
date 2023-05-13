package com.empmanagement.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoginFilter extends OncePerRequestFilter {
    private static final String SECRET_KEY = "q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2e3r4t5y6u7i8o9p0";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        System.out.println(path);
        if (path.startsWith("/users") &&!path.equals("/users/login")  && !path.equals("/users/register") && !path.equals("/users/logout")) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwtContent = authorizationHeader.substring(7);
                try {
                    Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtContent);
                } catch (JwtException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
