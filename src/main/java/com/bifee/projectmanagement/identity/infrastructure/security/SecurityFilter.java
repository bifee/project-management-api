package com.bifee.projectmanagement.identity.infrastructure.security;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityFilter(TokenService tokenService, UserDetailsServiceImpl userDetailsService ) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var email = tokenService.validateToken(token);
            if (email != null) {
                UserDetailsImpl userDetails = this.userDetailsService.loadUserByUsername(email);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        var token = authHeader.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
