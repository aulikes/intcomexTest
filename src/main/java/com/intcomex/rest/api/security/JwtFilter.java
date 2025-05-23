package com.intcomex.rest.api.security;

import com.intcomex.rest.api.util.DateTimeUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
//            String path = request.getRequestURI();
//            if (path.startsWith("/swagger-ui")
//                    || path.startsWith("/v3/api-docs")
//                    || path.startsWith("/swagger-resources")
//                    || path.startsWith("/webjars")
//                    || path.startsWith("/h2-console")
//                    || path.equals("/auth/login")) {
//                filterChain.doFilter(request, response);
//                return;
//            }

            String header = request.getHeader("Authorization");
            String token = null;
            String username = null;

            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                username = jwtUtil.extractUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            log.debug("Error de firma JWT: ", ex);
            handleJwtException(response, "Token inválido o alterado", request.getRequestURI());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.debug("Token expirado: ", ex);
            handleJwtException(response, "Token expirado", request.getRequestURI());
        } catch (Exception ex) {
            log.debug("Error inesperado en JwtFilter: ", ex);
            handleJwtException(response, "Error al procesar el token", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(HttpServletResponse response, String message, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
        {
          "timestamp": "%s",
          "status": 401,
          "error": "Unauthorized",
          "message": "%s",
          "path": "%s"
        }
    """.formatted(DateTimeUtils.now(), message, path));
    }
}
