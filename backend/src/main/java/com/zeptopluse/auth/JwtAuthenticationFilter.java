package com.zeptopluse.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserAccountRepository users;
    public JwtAuthenticationFilter(JwtService jwtService, UserAccountRepository users) { this.jwtService = jwtService; this.users = users; }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            JwtService.Claims claims = jwtService.parseAndValidate(header.substring(7).trim());
            if (claims != null) {
                users.findById(claims.userId()).filter(u -> u.getStatus() == AccountStatus.ACTIVE).ifPresent(user -> {
                    var auth = new UsernamePasswordAuthenticationToken(user.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        }
        filterChain.doFilter(request, response);
    }
}
