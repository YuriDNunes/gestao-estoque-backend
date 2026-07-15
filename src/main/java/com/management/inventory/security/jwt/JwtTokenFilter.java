package com.management.inventory.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider tokenProvider;

    public JwtTokenFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filter) throws ServletException, IOException {
        //Chama função para extrair token do header
        var token = tokenProvider.resolveToken( request);
        //Verifica se a variavel token esta vazia e se é um token valido
        if(StringUtils.hasText(token) && tokenProvider.validateToken(token)){
            //Chama autenticação para o token
            Authentication auth = tokenProvider.getAuthentication(token);
            if(auth != null){
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filter.doFilter(request, response);
    }
}
