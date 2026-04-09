package com.ecociclo.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtro que intercepta todas as requisições e valida o token Firebase
// O frontend deve enviar o header: Authorization: Bearer <firebase-id-token>
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Libera requisições OPTIONS (preflight do CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Token de autenticação não fornecido\"}");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Valida o token com o Firebase Auth e extrai as informações do usuário
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
            // Salva o UID do usuário no request para uso nos controllers
            request.setAttribute("firebaseUid", firebaseToken.getUid());
            request.setAttribute("firebaseEmail", firebaseToken.getEmail());
            filterChain.doFilter(request, response);
        } catch (FirebaseAuthException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Token inválido ou expirado\"}");
        }
    }
}
